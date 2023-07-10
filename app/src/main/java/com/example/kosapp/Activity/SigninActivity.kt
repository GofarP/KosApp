package com.example.kosapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private  var auth= FirebaseAuth.getInstance()
    private var user=FirebaseAuth.getInstance().currentUser
    private lateinit var preferenceManager:PreferenceManager

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private var database= FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@SigninActivity)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        locationListener=object:LocationListener{
            override fun onLocationChanged(p0: Location) {

            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(this@SigninActivity)

        checkLocationPermission()

        if(user!=null && preferenceManager.getString(Constant().KEY_ROLE)==Constant().KEY_ROLE_ADMIN)
        {
            startActivity(Intent(this@SigninActivity, AdminActivity::class.java))
            finish()
        }

        else if(user!=null && preferenceManager.getString(Constant().KEY_ROLE)==Constant().KEY_ROLE_USER)
        {
            startActivity(Intent(this@SigninActivity, MainActivity::class.java))
            finish()
        }


        binding.btnsignin.setOnClickListener{
            if(validation())
            {
                Helper().showToast(this@SigninActivity,"Silahkan Isi Kolom Yang Masih Kosong")
            }

            else
            {
                signIn()
            }

        }

        binding.lblsignup.setOnClickListener {
            startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
        }

    }

    private fun checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Izin belum diberikan, minta izin kepada pengguna
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        } else
        {
            // Izin telah diberikan, minta lokasi sekarang
            getLocation()
        }
    }

    private fun getLocation()
    {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // Dapatkan lokasi sekarang
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                locationListener
            )

        }

        else
        {
            Toast.makeText(this@SigninActivity, "Aktifkan GPS untuk mendapatkan lokasi sekarang", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validation():Boolean
    {
        var kosong=false
        val email=binding.txtemail.text.trim()
        val password=binding.txtpassword.text.trim()

        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            kosong=true
        }

        return kosong
    }



    private fun signIn()
    {
        val email=binding.txtemail.text.trim().toString()
        val password=binding.txtpassword.text.trim().toString()
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            database.child(Constant().KEY_USER).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        val dataEmail=snap.child(Constant().KEY_EMAIL).value.toString()
                        val dataRole=snap.child(Constant().KEY_ROLE).value.toString()

                        if(dataEmail==email && dataRole==Constant().KEY_ROLE_ADMIN)
                        {
                            preferenceManager.putString(Constant().KEY_ROLE,Constant().KEY_ROLE_ADMIN)
                            startActivity(Intent(this@SigninActivity, AdminActivity::class.java))
                            finish()
                        }

                        else if(dataEmail==email && dataRole==Constant().KEY_ROLE_USER)
                        {
                            preferenceManager.putString(Constant().KEY_ROLE,Constant().KEY_ROLE_USER)
                            startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                            finish()
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }

            })


        }.addOnFailureListener {
            Toast.makeText(this@SigninActivity, it.message, Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 // Interval waktu antara pembaruan lokasi (dalam milidetik)
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // Jarak minimum yang harus ditempuh agar pembaruan lokasi dilakukan (dalam meter)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, dapatkan lokasi terkini
                getLocation()
            } else {
                Toast.makeText(this@SigninActivity, "Silahkan Izinkan Perizinan lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

}