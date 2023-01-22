package com.example.kosapp.Helper

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.kosapp.Activity.SigninActivity
import com.example.kosapp.R

class Helper {

    fun showToast(context: Context, message:String)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun setStatusBarColor(activity: Activity)
    {
        var window=activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(activity, R.color.main_color)
    }
}