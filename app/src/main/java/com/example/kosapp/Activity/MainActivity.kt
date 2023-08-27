package com.example.kosapp.Activity

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private var auth=FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Helper().setStatusBarColor(this@MainActivity)
        val navController=Navigation.findNavController(this,R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomnav, navController)
        if(auth==null)
        {
            binding.bottomnav.visibility= View.GONE
        }
        else
        {
            binding.bottomnav.setupWithNavController(navController)
        }

        
    }

}