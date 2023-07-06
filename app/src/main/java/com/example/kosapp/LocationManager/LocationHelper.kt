package com.example.kosapp.LocationManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point

class LocationHelper(val context: Context): PermissionsListener, LocationEngineListener {
    private lateinit var locationEngine: LocationEngine
    private lateinit var lokasiSekarang: Location
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var lokasiSekarangLatLng: Point
    private lateinit var lokasiKosLatLng: Point


    @SuppressLint("MissingPermission")
    fun ambilLokasiSekarang():Location
    {
        locationEngine= LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
        lokasiSekarang=locationEngine.lastLocation

        return lokasiSekarang
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted)
        {
            enableLocation(context)
        }
    }

    override fun onConnected() {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location?) {
        TODO("Not yet implemented")
    }

    private fun enableLocation(context: Context)
    {
        if(PermissionsManager.areLocationPermissionsGranted(context))
        {
            initializeLocationEngine(context)
        }

        else
        {
            val activity = context as Activity

            permissionsManager= PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine(context: Context)
    {
        locationEngine= LocationEngineProvider(context).obtainBestLocationEngineAvailable()
        locationEngine?.priority= LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation=locationEngine.lastLocation

        if(lastLocation != null)
        {
            lokasiSekarang=lastLocation
        }

        else
        {
            locationEngine?.addLocationEngineListener(this)
        }
    }

}