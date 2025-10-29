package com.project.smartwasteo.worker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

data class RoutePoint(val latitude: Double, val longitude: Double)

@Serializable
data class OSRMResponse(
    val code: String,
    val routes: List<Route>? = null
)

@Serializable
data class Route(
    val geometry: String,
    val distance: Double,
    val duration: Double
)

class WorkerViewModel : ViewModel() {

    private val _routePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints

    private val _isLoadingRoute = MutableStateFlow(false)
    val isLoadingRoute: StateFlow<Boolean> = _isLoadingRoute

    private val _routeError = MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError

    init {
        // Example: Load route on initialization
        loadRouteFromOSRM(
            coordinates = listOf(
                RoutePoint(26.855758, 75.820475),  // Jaipur
                RoutePoint(26.853973, 75.819995),  // Another point

                RoutePoint(26.848938, 75.819920),

            // Another point
            )
        )
    }

    fun loadRouteFromOSRM(coordinates: List<RoutePoint>) {
        viewModelScope.launch {
            try {
                _isLoadingRoute.value = true
                _routeError.value = null

                // Build OSRM API URL
                val coordString = coordinates.joinToString(";") {
                    "${it.longitude},${it.latitude}"
                }

                val url = "https://router.project-osrm.org/route/v1/driving/$coordString?overview=full&geometries=polyline"

                Log.d("WorkerViewModel", "Fetching route from: $url")

                // Use Dispatchers.IO for network operations
                val response = withContext(Dispatchers.IO) {
                    URL(url).readText()
                }

                Log.d("WorkerViewModel", "Response received")

                val json = Json { ignoreUnknownKeys = true }
                val osrmResponse = json.decodeFromString<OSRMResponse>(response)

                if (osrmResponse.code == "Ok" && osrmResponse.routes?.isNotEmpty() == true) {
                    val geometry = osrmResponse.routes[0].geometry
                    val decodedPoints = decodePolyline(geometry)
                    _routePoints.value = decodedPoints
                    Log.d("WorkerViewModel", "Route loaded with ${decodedPoints.size} points")
                } else {
                    _routeError.value = "No route found"
                    Log.e("WorkerViewModel", "OSRM response code: ${osrmResponse.code}")
                }

            } catch (e: Exception) {
                _routeError.value = "Failed to load route: ${e.message}"
                Log.e("WorkerViewModel", "Error loading route", e)
            } finally {
                _isLoadingRoute.value = false
            }
        }
    }

    private fun decodePolyline(encoded: String): List<RoutePoint> {
        val poly = ArrayList<RoutePoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latitude = lat.toDouble() / 1E5
            val longitude = lng.toDouble() / 1E5
            poly.add(RoutePoint(latitude, longitude))
        }

        return poly
    }
}