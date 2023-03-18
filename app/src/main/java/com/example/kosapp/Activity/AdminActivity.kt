package com.example.kosapp.Activity

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kosapp.Adapter.PagerAdapter.AdminPagerAdapter
import com.example.kosapp.Adapter.PagerAdapter.HomePagerAdapter
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityAdminBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.Help

class AdminActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(this@AdminActivity)
        Helper().setStatusBarColor(this@AdminActivity)

        binding.viewPagerAdmin.adapter= AdminPagerAdapter(this@AdminActivity)
        TabLayoutMediator(binding.tabLayoutAdmin, binding.viewPagerAdmin){tab, index->
            tab.text=when(index){
                0->{"Verifikasi"}
                1->{"Kos"}
                2->{"Pengguna"}
                else->{throw Resources.NotFoundException("Posisi Tidak DItemukan")}
            }
        }.attach()

        binding.ivlogout.setOnClickListener {
            logOut()
        }
    }


    private fun logOut()
    {
        FirebaseAuth.getInstance().signOut()
        preferenceManager.clear()
        Toast.makeText(this@AdminActivity, "Sukses Logout", Toast.LENGTH_SHORT).show()
        finish()
        startActivity(Intent(this@AdminActivity,SigninActivity::class.java))
    }


}