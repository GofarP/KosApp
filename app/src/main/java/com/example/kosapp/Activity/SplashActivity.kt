 package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import java.util.*
import kotlin.concurrent.schedule

 class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Helper().setStatusBarColor(this@SplashActivity)

        Timer().schedule(2000) {
            startActivity(Intent(this@SplashActivity, SigninActivity::class.java))
            finish()
        }
    }
}