package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailKosSayaBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class DetailKosSayaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailKosSayaBinding
    private val slideImageArrayList=ArrayList<SlideModel>()
    private var database=Firebase.database.reference
    private lateinit var permintaan:Permintaan

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

        binding.btncancel.setOnClickListener {
            keluarKos()
        }

    }


    private fun keluarKos()
    {
//        permintaan= Permintaan(
//            idPermintaan= UUID.randomUUID().toString(),
//            idKos=kos.id,
//            namaKos=kos.nama,
//            dari = emailPengguna,
//            kepada = kos.emailPemilik,
//            judul = "Permintaan Sewa Kos",
//            isi ="Mengajukan Permintaan Untuk Menyewa Kos",
//            tanggal = Date()
//        )
//
//        database.child(Constant().PERMINTAAN)
//            .push()
//            .ref.setValue(permintaan)
//            .addOnSuccessListener {
//                Toast.makeText(this@DetailKosSayaActivity, "Sukses Mengajukan Permintaan Untuk Keluar Kos", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this@DetailKosSayaActivity, "Gagal Mengajukan Permintaan Untuk Keluar Kos", Toast.LENGTH_SHORT).show()
//            }
    }
}