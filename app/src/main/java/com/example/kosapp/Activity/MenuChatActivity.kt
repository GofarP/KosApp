package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R

class MenuChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_chat_activity)

        Helper().setStatusBarColor(this@MenuChatActivity)
    }
}