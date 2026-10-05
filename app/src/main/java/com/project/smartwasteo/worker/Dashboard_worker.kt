package com.project.smartwasteo.worker

import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.geometry.LatLngBounds

import android.graphics.Color as AndroidColor

@Composable
fun Dashboard_worker(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WorkerViewModel = viewModel()
) {
    val points by viewModel.routePoints.collectAsState()
    val mapView = rememberMapViewWithLifecycle()
    val isLoadingRoute by viewModel.isLoadingRoute.collectAsState()
    val routeError by viewModel.routeError.collectAsState()


    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    val complaintPoints by viewModel.complaintPoints.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.getMapAsync { map ->
                    mapLibreMap = map

                    // Stadia map api key
                    val stadiaApiKey = "34169669-6b9c-4b66-9366-26ae8a66e012"
                    val styleUrl = "https://tiles.stadiamaps.com/styles/alidade_smooth.json?api_key=$stadiaApiKey"

                    map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                        Log.d("Dashboard", "Map style loaded")

                        if (points.isNotEmpty()) {
                            drawRoute(map, points,complaintPoints)
                        }
                    }
                }
            }
        )

        LaunchedEffect(points, complaintPoints,mapLibreMap) {
            if ((points.isNotEmpty() || complaintPoints.isNotEmpty()) && mapLibreMap != null) {
                mapLibreMap?.getStyle { style ->
                    if (style.isFullyLoaded) {
                        drawRoute(mapLibreMap!!, points, complaintPoints)
                    }
                }
            }
        }

        if (isLoadingRoute) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        routeError?.let { error ->
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = error)
            }
        }
    }
}

private fun drawRoute(map: MapLibreMap, points: List<RoutePoint>,complaintPoints: List<RoutePoint>) {

    map.clear()

    if (points.isEmpty()) return

    val latLngList = points.map { LatLng(it.latitude, it.longitude) }
    // Draw route line
    val polylineOptions = PolylineOptions()
        .addAll(latLngList)
        .color(AndroidColor.BLUE)
        .width(8f)

    map.addPolyline(polylineOptions)

    complaintPoints.forEachIndexed { index, complaint ->
        val location = LatLng(
            complaint.latitude,
            complaint.longitude
        )

        map.addMarker(
            MarkerOptions()
                .position(location)
                .title("Complaint ${index + 1}")
        )
    }
    if (latLngList.size >= 2) {
        val boundsBuilder = LatLngBounds.Builder()

        latLngList.forEach { point ->
            boundsBuilder.include(point)
        }

        val bounds = boundsBuilder.build()

        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 80)
        )
    } else {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLngList.first(), 15.0)
        )
    }

    Log.d("Dashboard", "✅ Route drawn with ${latLngList.size} points")
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = View.generateViewId()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    return mapView
}