package com.example.kosapp.Activity

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kosapp.Adapter.PagerAdapter.MyKosPagerAdapter
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityKosSayaBinding
import com.google.android.material.tabs.TabLayoutMediator

class KosSayaActivity : AppCompatActivity() {
    private lateinit var binding:ActivityKosSayaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKosSayaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@KosSayaActivity)

        binding.viewPagerKosSaya.adapter=MyKosPagerAdapter(this@KosSayaActivity)
        TabLayoutMediator(binding.tabLayoutKosSaya, binding.viewPagerKosSaya){
            tab,index->
            tab.text=when(index)
            {
                0->{"Menyewa"}
                1->{"Disewa"}
                2->{"Permintaan"}
                else->{throw Resources.NotFoundException("Posisi Tidak DItemukan")}
            }
        }.attach()

        binding.btntest.setOnClickListener {
            startActivity(Intent(this@KosSayaActivity, DetailKosSayaActivity::class.java))
        }

    }
}