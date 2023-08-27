 package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.schedule

 class SplashActivity : AppCompatActivity() {

     private lateinit var preferenceManager: PreferenceManager

     private var auth=FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Helper().setStatusBarColor(this@SplashActivity)

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(this@SplashActivity)

        Timer().schedule(2000) {

            if(auth!=null && preferenceManager.getString(Constant().KEY_ROLE)== Constant().KEY_ROLE_ADMIN)
            {
                startActivity(Intent(this@SplashActivity, AdminActivity::class.java))
                finish()
            }

            else if(auth!=null && preferenceManager.getString(Constant().KEY_ROLE)== Constant().KEY_ROLE_USER)
            {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        }
    }
}