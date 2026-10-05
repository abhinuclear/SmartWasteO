package com.project.smartwasteo.worker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class OSRMTripResponse(
    val code: String,
    val trips: List<OSRMTrip>? = null
)

@Serializable
data class OSRMTrip(
    val geometry: String,
    val distance: Double,
    val duration: Double
)

class WorkerViewModel : ViewModel() {

    private val _routePoints =
        MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints

    private val _isLoadingRoute = MutableStateFlow(false)
    val isLoadingRoute: StateFlow<Boolean> = _isLoadingRoute

    private val _routeError =
        MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError

    val _complaintPoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val complaintPoints: StateFlow<List<RoutePoint>> = _complaintPoints

    private val databaseRef =
        FirebaseDatabase.getInstance().getReference("complaints")

    private var complaintsListener: ValueEventListener? = null
    private var complaintsQuery: Query? = null

    private val json = Json {
        ignoreUnknownKeys = true
    }

    init {
        loadComplaints()
    }

    private fun loadComplaints() {
        complaintsQuery = databaseRef

        complaintsListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val complaintPoints = mutableListOf<RoutePoint>()

                for (complaint in snapshot.children) {

                    val status = complaint.child("status")
                        .getValue(String::class.java)

                    if (
                        status.equals("completed", ignoreCase = true) ||
                        status.equals("cancelled", ignoreCase = true)
                    ) {
                        continue
                    }

                    val latitude = complaint.child("latitude")
                        .value?.toString()?.toDoubleOrNull()

                    val longitude = complaint.child("longitude")
                        .value?.toString()?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        complaintPoints.add(
                            RoutePoint(latitude, longitude)
                        )
                    } else {
                        Log.w(
                            "WorkerViewModel",
                            "Missing coordinates: ${complaint.key}"
                        )
                    }
                }
                _complaintPoints.value = complaintPoints




                if (complaintPoints.size < 2) {
                    _routePoints.value = emptyList()
                    _routeError.value =
                        "At least two complaints with valid locations are needed."
                    return
                }

                loadRouteFromOSRM(complaintPoints)
            }

            override fun onCancelled(error: DatabaseError) {
                _routeError.value =
                    "Could not load complaints: ${error.message}"

            }
        }

        complaintsQuery?.addValueEventListener(complaintsListener!!)
    }
    private fun loadRouteFromOSRM(coordinates: List<RoutePoint>) {
        viewModelScope.launch {
            _isLoadingRoute.value = true
            _routeError.value = null

            try {
                val decodedPoints = withContext(Dispatchers.IO) {
                    val coordinateString =
                        coordinates.joinToString(";") {
                            "${it.longitude},${it.latitude}"
                        }

                    // Trip API
                    val url =
                        "https://router.project-osrm.org/trip/v1/driving/" +
                                "$coordinateString" +
                                "?roundtrip=false&source=first&destination=last" +
                                "&overview=full&geometries=polyline"

                    Log.d("WorkerViewModel", "OSRM Trip URL: $url")

                    val connection =
                        URL(url).openConnection() as HttpURLConnection

                    try {
                        connection.requestMethod = "GET"
                        connection.connectTimeout = 15000
                        connection.readTimeout = 15000

                        // Identify your app to the server
                        connection.setRequestProperty(
                            "User-Agent",
                            "SmartWasteO/1.0 (Android)"
                        )
                        connection.setRequestProperty("Accept", "application/json")

                        val responseCode = connection.responseCode

                        // Read the error response too, so we can see why it failed
                        val responseStream =
                            if (responseCode in 200..299) {
                                connection.inputStream
                            } else {
                                connection.errorStream
                            }

                        val response = responseStream
                            ?.bufferedReader()
                            ?.use { it.readText() }
                            ?: ""

                        Log.d("WorkerViewModel", "OSRM HTTP code: $responseCode")
                        Log.d("WorkerViewModel", "OSRM response: $response")

                        if (responseCode !in 200..299) {
                            throw Exception("OSRM HTTP error: $responseCode - $response")
                        }
                        val tripResponse =
                            json.decodeFromString<OSRMTripResponse>(response)

                        if (tripResponse.code != "Ok") {
                            throw Exception(
                                "OSRM returned: ${tripResponse.code}"
                            )
                        }

                        val geometry = tripResponse.trips
                            ?.firstOrNull()
                            ?.geometry
                            ?: throw Exception("No trip geometry returned.")

                        decodePolyline(geometry)
                    } finally {
                        connection.disconnect()
                    }
                }

                if (decodedPoints.isEmpty()) {
                    _routeError.value = "The trip route is empty."
                } else {
                    _routePoints.value = decodedPoints
                    Log.d(
                        "WorkerViewModel",
                        "OSRM trip loaded with ${decodedPoints.size} route points"
                    )
                }

            } catch (e: Exception) {
                _routeError.value = "Failed to load trip: ${e.message}"
                Log.e("WorkerViewModel", "Trip API error", e)
            } finally {
                _isLoadingRoute.value = false
            }
        }
    }

    // Decodes OSRM's encoded polyline into latitude/longitude points.
    private fun decodePolyline(encoded: String): List<RoutePoint> {
        val points = mutableListOf<RoutePoint>()

        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var shift = 0
            var result = 0
            var b: Int

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val deltaLat =
                if ((result and 1) != 0) (result shr 1).inv()
                else result shr 1

            lat += deltaLat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val deltaLng =
                if ((result and 1) != 0) (result shr 1).inv()
                else result shr 1

            lng += deltaLng

            points.add(
                RoutePoint(
                    latitude = lat / 1E5,
                    longitude = lng / 1E5
                )
            )
        }

        return points
    }

    override fun onCleared() {
        super.onCleared()

        complaintsListener?.let { listener ->
            complaintsQuery?.removeEventListener(listener)
        }
    }
}