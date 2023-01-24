package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R

class DetailSewaKosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sewa_kos)

        Helper().setStatusBarColor(this@DetailSewaKosActivity)

    }
}