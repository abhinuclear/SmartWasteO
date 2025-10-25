package com.project.smartwasteo.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class WorkerViewModel : ViewModel() {

    private val _routePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints

    init {
        fetchPointsFromFirebase()
    }

    fun fetchPointsFromFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("complaints")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coords = snapshot.children.mapNotNull {
                    val lat = it.child("latitude").getValue(Double::class.java)
                    val lon = it.child("longitude").getValue(Double::class.java)
                    if (lat != null && lon != null) RoutePoint(lat, lon) else null
                }
                getOptimizedRouteFromOSRM(coords)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getOptimizedRouteFromOSRM(points: List<RoutePoint>) {
        if (points.isEmpty()) return

        val coordsStr = points.joinToString(";") { "${it.longitude},${it.latitude}" }
        val url = "https://router.project-osrm.org/trip/v1/driving/$coordsStr?roundtrip=true&source=first"

        viewModelScope.launch {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@launch
                val route = Json.decodeFromString<OSRMTripResponse>(body)

                val sortedPoints = route.waypoints
                    .sortedBy { it.waypoint_index }
                    .map { RoutePoint(it.location[1], it.location[0]) }


                _routePoints.value = sortedPoints

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
fun buildRouteGeoJson(points: List<RoutePoint>): LineString {
    val coordinates = points.map { Point.fromLngLat(it.longitude, it.latitude) }
    return LineString.fromLngLats(coordinates)
}
