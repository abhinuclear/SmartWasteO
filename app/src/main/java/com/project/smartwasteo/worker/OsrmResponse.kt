package com.project.smartwasteo.worker

import kotlinx.serialization.Serializable

@Serializable
data class OSRMTripResponse(
    val trips: List<Trip>,
    val waypoints: List<Waypoint>
)

@Serializable
data class Trip(
    val geometry: String
)

@Serializable
data class Waypoint(
    val location: List<Double>, // [lon, lat]
    val waypoint_index: Int
)

data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)
