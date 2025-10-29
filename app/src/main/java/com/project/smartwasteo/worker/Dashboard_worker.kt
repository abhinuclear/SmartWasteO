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

import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.annotations.PolylineOptions

import com.project.smartwasteo.AuthViewModel
import android.graphics.Color as AndroidColor

@Composable
fun Dashboard_worker(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: WorkerViewModel = viewModel()
) {
    val context = LocalContext.current
    val points by viewModel.routePoints.collectAsState()
    val mapView = rememberMapViewWithLifecycle()
    val isLoadingRoute by viewModel.isLoadingRoute.collectAsState()
    val routeError by viewModel.routeError.collectAsState()

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map
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

                        // Draw route if points are available
                        if (points.isNotEmpty()) {
                            drawRoute(map, points)
                        }
                    }
                }
            }
        )

        // Draw route when points change
        LaunchedEffect(points) {
            if (points.isNotEmpty() && mapLibreMap != null) {
                mapLibreMap?.getStyle { style ->
                    if (style.isFullyLoaded) {
                        drawRoute(mapLibreMap!!, points)
                    }
                }
            }
        }

        // Loading indicator
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

        // Error message
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

private fun drawRoute(map: MapLibreMap, points: List<RoutePoint>) {
    // Clear existing polylines
    map.clear()

    if (points.isEmpty()) return

    val latLngList = points.map { LatLng(it.latitude, it.longitude) }
    val firstLatLng = latLngList.first()

    // Move camera to first location
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 15.0))

    // Draw route line
    val polylineOptions = PolylineOptions()
        .addAll(latLngList)
        .color(AndroidColor.BLUE)
        .width(8f)

    map.addPolyline(polylineOptions)

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