package com.example.kosapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.mapbox.geojson.Point
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityTestMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
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
import javax.security.auth.callback.PasswordCallback

class TestMapActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener, MapboxMap.OnMapClickListener{

    private lateinit var binding:ActivityTestMapBinding
    private lateinit var mapView:MapView
    private lateinit var map: MapboxMap
    private lateinit var startButton:Button
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation:Location
    private lateinit var originPosition: Point
    private lateinit var destinationPosition: Point

    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin:LocationLayerPlugin?=null
    private var destinationMarker:Marker?=null
    private var navigationMapRoute:NavigationMapRoute?=null

    private val LOCATION_REQUEST_CODE=1

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTestMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView=findViewById(R.id.mapView)
        startButton=findViewById(R.id.button)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapBoxMap->
            map=mapBoxMap
            map.addOnMapClickListener(this)
            enableLocation()
        }

        startButton.setOnClickListener {
            val options=NavigationLauncherOptions.builder()
                .origin(originPosition)
                .destination(destinationPosition)
                .shouldSimulateRoute(false)
                .build()
            NavigationLauncher.startNavigation(this, options)
        }

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
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
        locationLayerPlugin=LocationLayerPlugin(mapView, map, locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode=CameraMode.TRACKING
        locationLayerPlugin?.renderMode=RenderMode.NORMAL
    }

    private fun setCameraPosition(location:Location)
    {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
            13.0
        ))
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation()
    {
        val locationTask=fusedLocationProviderClient.lastLocation

        locationTask.addOnSuccessListener {location->
            if(location!=null){
                Log.d("onSuccess",location.toString())
                Log.d("Lattitude",location.latitude.toString())
                Log.d("Longitude",location.longitude.toString())
            }
            else{
                Log.d("onSuccess","Location Was Null")
            }
        }

        locationTask.addOnFailureListener {e->
            Log.e("on Failure:",e.localizedMessage)
        }
    }

    private fun askLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this@TestMapActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d("Tag","Ask Location Permission")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE)

            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
            }
        }
    }


    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }

        if(ContextCompat.checkSelfPermission(this@TestMapActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            getLastLocation()
        }
        else
        {

        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(outState!=null)
        {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(applicationContext, "Need To Enbale Permission",Toast.LENGTH_SHORT).show()
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
        if(requestCode==LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //permission granted
                getLastLocation()
            }else{
                askLocationPermission()
            }
        }

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
                        navigationMapRoute= NavigationMapRoute(null, mapView, map)
                    }

                    navigationMapRoute?.addRoute(body.routes().first())

                    val route = response.body()?.routes()?.get(0)
                    val distance = route?.distance()

                    Toast.makeText(applicationContext, distance.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.e("MainActivity","Error ${t?.message}")
                }

            })
    }

    override fun onMapClick(point: LatLng) {

        destinationMarker?.let {
            map.removeMarker(it)
        }
        destinationMarker=map.addMarker(MarkerOptions().position(point))
        destinationPosition=Point.fromLngLat(point.longitude, point.latitude)
        originPosition=Point.fromLngLat(originLocation.longitude, originLocation.latitude)

        getRoute(originPosition, destinationPosition)

        startButton.isEnabled=true
//        startButton.setBackgroundResource(R.color.main_color)

    }




}