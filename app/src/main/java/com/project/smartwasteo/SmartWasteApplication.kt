package com.project.smartwasteo

import android.app.Application
import android.util.Log
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

//import org.maplibre.android.*

class SmartWasteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("SmartWasteApp", "Application started")

        try {
            // Try with WellKnownTileServer
            MapLibre.getInstance(
                applicationContext,
                null,
                WellKnownTileServer.MapLibre
            )
            Log.d("SmartWasteApp", "MapLibre initialized with WellKnownTileServer")
        } catch (e: Exception) {
            // Fallback to simple initialization
            MapLibre.getInstance(applicationContext)
            Log.d("SmartWasteApp", "MapLibre initialized (fallback)")
        }
    }
}