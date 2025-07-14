package com.project.smartwasteo

data class Complaint(
    var name: String =" ",
    var mobno: String ="",
    var address: String ="",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    // val timestamp: Long = 0L,
    val time: String = ""
)