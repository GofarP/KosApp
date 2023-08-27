package com.example.kosapp.Activity

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityRouteJalanBinding
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
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RouteJalanActivity : AppCompatActivity(),  PermissionsListener, LocationEngineListener {

    private lateinit var binding:ActivityRouteJalanBinding
    private lateinit var map: MapboxMap
    private lateinit var startButton:Button
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation:Location
    private lateinit var originPosition: Point
    private lateinit var destinationPosition: Point
    private lateinit var latLng: LatLng

    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin:LocationLayerPlugin?=null
    private var destinationMarker:Marker?=null
    private var navigationMapRoute:NavigationMapRoute?=null
    private lateinit var lattitude:String
    private lateinit var longitude:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRouteJalanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))

        lattitude=intent.getStringExtra("lattitude").toString()
        longitude=intent.getStringExtra("longitude").toString()

        latLng=LatLng()
        latLng.latitude= lattitude.toDouble()
        latLng.longitude= longitude.toDouble()


        binding.mapviewroute.getMapAsync { mapBox->
            map=mapBox
            enableLocation()

            destinationMarker=map.addMarker(MarkerOptions().position(latLng))
            destinationPosition=Point.fromLngLat(latLng.longitude, latLng.latitude)
            originPosition=Point.fromLngLat(originLocation.longitude, originLocation.latitude)

            getRoute(originPosition, destinationPosition)

            mapBox.cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(20.0)
                .build()
        }

        binding.btnroute.setOnClickListener {
            try{
                destinationPosition=Point.fromLngLat(longitude.toDouble() , lattitude.toDouble())
                originPosition=Point.fromLngLat(originLocation.longitude,originLocation.latitude)
                val options=NavigationLauncherOptions.builder()
                    .origin(originPosition)
                    .destination(destinationPosition)
                    .shouldSimulateRoute(false)
                    .build()
                NavigationLauncher.startNavigation(this, options)
            }

            catch (e:Exception)
            {
                Log.d("Route Exception",e.message.toString())
            }

        }

    }


    private fun enableLocation(){
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
        locationEngine=LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority=LocationEnginePriority.HIGH_ACCURACY
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
        locationLayerPlugin=LocationLayerPlugin(binding.mapviewroute, map, locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode= CameraMode.TRACKING
        locationLayerPlugin?.renderMode= RenderMode.NORMAL
    }

    private fun setCameraPosition(location:Location)
    {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
            13.0
        ))
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapviewroute.onLowMemory()
    }


    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()

        locationEngine?.requestLocationUpdates()
        locationLayerPlugin?.onStart()

        binding.mapviewroute.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapviewroute.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapviewroute.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapviewroute.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapviewroute.onDestroy()
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation=location
            setCameraPosition(location)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this@RouteJalanActivity, "Aktifkan Permission Map Terlebih Dahulu", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted)
        {
            enableLocation()
        }
    }

    private fun getRoute(origin:Point, destination:Point)
    {
        NavigationRoute.builder().accessToken(Mapbox.getAccessToken())
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object: Callback<DirectionsResponse>{
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                )
                {
                    val routeResponse=response?:return
                    val body=routeResponse.body()?:return
                    if(body.routes().isEmpty())
                    {
                        Log.e("MainActivity","No Routes Found")
                        return
                    }

                    if(navigationMapRoute!=null)
                    {
                        navigationMapRoute?.removeRoute()
                    }

                    else
                    {
                        navigationMapRoute= NavigationMapRoute(null, binding.mapviewroute, map)
                    }

                    navigationMapRoute?.addRoute(body.routes().first())

                    val route = response.body()?.routes()?.get(0)
                    val distance = route?.distance()

                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("RouteJalanActivity","Error ${t?.message}")
                }

            })
    }


}