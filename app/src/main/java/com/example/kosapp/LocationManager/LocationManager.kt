package com.example.kosapp.LocationManager

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationManager(private val context: Context) {

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    var lattitude=0.0
    var longitude=0.0



        fun getLocation(callback: LocationCallback) {
            this.locationCallback = callback

            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
                return
            }



            // Lakukan pengambilan nilai latitude dan longitude seperti yang Anda lakukan sebelumnya
            val location = fusedLocationProviderClient.lastLocation
            location.addOnSuccessListener { loc ->
                if (loc != null) {
                    val latitude = loc.latitude
                    val longitude = loc.longitude
                    locationCallback?.onLocationReceived(latitude, longitude)
                }
            }
        }


    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
    }

}