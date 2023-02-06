package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Helper().setStatusBarColor(this@MainActivity)

        val navController=Navigation.findNavController(this,R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomnav, navController)
        binding.bottomnav.setupWithNavController(navController)

    }



}