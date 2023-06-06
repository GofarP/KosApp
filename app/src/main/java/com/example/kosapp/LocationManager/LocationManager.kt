package com.example.kosapp.LocationManager

import android.content.Context
import android.location.Location
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point

class LocationManager{
    private lateinit var locationEngine: LocationEngine
    private lateinit var lokasiSekarang: Location
    private lateinit var lokasiSekarangLatLng: Point
    private lateinit var lokasiKosLatLng: Point

    fun ambilLokasiSekarang(context: Context):Location
    {
        locationEngine= LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()
        @SuppressWarnings("MissingPermission")
        lokasiSekarang=locationEngine.lastLocation

        return lokasiSekarang
    }
}