package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@SigninActivity)

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


}