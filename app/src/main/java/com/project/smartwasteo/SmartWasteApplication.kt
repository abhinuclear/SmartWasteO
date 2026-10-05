package com.project.smartwasteo

import android.app.Application
import android.util.Log
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer


class SmartWasteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("SmartWasteApp", "Application started")

        try {
            MapLibre.getInstance(
                applicationContext,
                null,
                WellKnownTileServer.MapLibre
            )
            Log.d("SmartWasteApp", "MapLibre initialized")
        } catch (e: Exception) {
            Log.e("SmartWasteApp", "MapLibre initialized (fallback)",e)
            MapLibre.getInstance(applicationContext)
        }
    }
}