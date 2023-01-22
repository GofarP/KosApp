package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailKosSayaBinding
import java.lang.Exception

class DetailKosSayaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailKosSayaBinding
    private val slideImageArrayList=ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailKosSayaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@DetailKosSayaActivity)

        slideImageArrayList.add(SlideModel(imageUrl = "https://bit.ly/2BteuF2",scaleType = ScaleTypes.FIT))
        slideImageArrayList.add(SlideModel(imageUrl = "https://i.postimg.cc/SK8kvj0y/mobile-devices.jpg",scaleType = ScaleTypes.FIT))
        slideImageArrayList.add(SlideModel(R.drawable.ic_sample_product2,scaleType = ScaleTypes.FIT))
        slideImageArrayList.add(SlideModel("https://bit.ly/3fLJf72",scaleType = ScaleTypes.FIT))

        binding.includeLayoutDetail.sliderDetailKos.setImageList(slideImageArrayList)




    }
}