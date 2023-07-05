package com.example.kosapp.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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

    private var database= FirebaseDatabase.getInstance().reference

    val LOCATION_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@SigninActivity)

        requestPermissionLocation()

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(this@SigninActivity)

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


    private fun requestPermissionLocation() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val grantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (grantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                grantedPermissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Izin lokasi diberikan, lakukan tindakan yang diperlukan
            } else {
                // Izin lokasi tidak diberikan
            }
        }
    }
}