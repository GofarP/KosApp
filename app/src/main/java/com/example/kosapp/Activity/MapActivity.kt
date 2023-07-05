package com.example.kosapp.Activity

import android.app.Activity
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityMapBinding
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener, MapboxMap.OnMapClickListener {

    private lateinit var binding:ActivityMapBinding
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation:Location

    private lateinit var map: MapboxMap
    private lateinit var returnIntent: Intent

    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin: LocationLayerPlugin?=null
    private var destinationMarker: Marker?=null

    private var longitudeKos=""
    private var latitudeKos=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapviewtambahlokasi.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))

        Helper().setStatusBarColor(this@MapActivity)

        binding.mapviewtambahlokasi.getMapAsync { mapBoxMap->
            map=mapBoxMap
            map.addOnMapClickListener(this)
            enableLocation()
        }

        binding.btnpilihlokasi.setOnClickListener {
            returnIntent=Intent()

            if(!longitudeKos.isNullOrEmpty() && !latitudeKos.isNullOrEmpty())
            {
                returnIntent.putExtra("longitudeKos",longitudeKos)
                returnIntent.putExtra("lattitudeKos",latitudeKos)
            }

            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        }

    }

    private fun enableLocation()
    {
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            initializeLocationEngine()
            initializeLocationLayer()
        }

        else
        {
            permissionsManager= PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine()
    {
        locationEngine= LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority= LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation=locationEngine?.lastLocation

        if(lastLocation != null)
        {
            originLocation=lastLocation
            setCameraPosition(lastLocation)
        }

        else
        {
            locationEngine?.addLocationEngineListener(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer()
    {
        locationLayerPlugin=LocationLayerPlugin(binding.mapviewtambahlokasi, map, locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode= CameraMode.TRACKING
        locationLayerPlugin?.renderMode= RenderMode.NORMAL
    }

    private fun setCameraPosition(location:Location)
    {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
            30.0
        ))
    }



    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }
        binding.mapviewtambahlokasi.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapviewtambahlokasi.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapviewtambahlokasi.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        binding.mapviewtambahlokasi.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapviewtambahlokasi.onDestroy()
        locationEngine?.deactivate()
    }


    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(applicationContext, "Need To Enable Permission", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted)
        {
            enableLocation()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { originLocation=location
            setCameraPosition(location)
        }
    }


    override fun onMapClick(point: LatLng) {

        destinationMarker?.let {
            map.removeMarker(it)
        }

        destinationMarker=map.addMarker(MarkerOptions().position(point))
        longitudeKos=point.longitude.toString()
        latitudeKos=point.latitude.toString()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        returnIntent=Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }


}