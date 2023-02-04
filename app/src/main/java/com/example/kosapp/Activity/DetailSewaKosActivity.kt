package com.example.kosapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.SetImageListCallback
import com.example.kosapp.Callback.SewaKosCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.databinding.ActivityDetailSewaKosBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailSewaKosActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailSewaKosBinding
    private var slideArrayList=ArrayList<SlideModel>()
    private var storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private var emailPengguna=Firebase.auth.currentUser?.email.toString()
    private lateinit var dataKosIntent:Intent
    private lateinit var kos:Kos
    private lateinit var permintaan: Permintaan
    private var permintaanDitemukan=false
    private var kosSudahDisewa=false
    private var calendar=Calendar.getInstance()
    private lateinit var tanggalHari:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailSewaKosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tanggalHari=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        Helper().setStatusBarColor(this@DetailSewaKosActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!
        setDataKos()
        
        checkSewaKosCallback(object : SewaKosCallback {
            override fun permintaanDitemukan(found: Boolean) {
                permintaanDitemukan=true
            }

            override fun kosSudahDisewa(found: Boolean) {
                kosSudahDisewa=true
            }

        })


        setGambarKos(object :SetImageListCallback{
            override fun setImageList(arrayListImage: ArrayList<SlideModel>) {
                binding.includeLayoutDetail.sliderDetailKos.setImageList(arrayListImage)
            }

        })

        binding.btnaddcomment.setOnClickListener {
            startActivity(Intent(this@DetailSewaKosActivity,CommentActivity::class.java))
        }

        binding.btnpesan.setOnClickListener {


            if(emailPengguna==kos.emailPemilik)
            {
                Toast.makeText(this@DetailSewaKosActivity, "Anda Tidak Bisa Menyewa Kos Anda Sendiri", Toast.LENGTH_SHORT).show()
            }

            else if(permintaanDitemukan)
            {
                Toast.makeText(this@DetailSewaKosActivity, "Permintaan Anda Sedang Di Proses", Toast.LENGTH_SHORT).show()
            }

            else if(kosSudahDisewa)
            {
                Toast.makeText(this@DetailSewaKosActivity, "Kos Ini Sudah Anda Sewa", Toast.LENGTH_SHORT).show()
            }

            else
            {
                sewaKos()
            }

        }

        binding.btnaddcomment.setOnClickListener {
            startActivity(Intent(this@DetailSewaKosActivity, TestActivity::class.java))
        }


    }

    private fun sewaKos()
    {

        permintaan=Permintaan(
            idPermintaan=UUID.randomUUID().toString(),
            idKos=kos.idKos,
            namaKos=kos.nama,
            dari = emailPengguna,
            kepada = kos.emailPemilik,
            judul = Constant().PERMINTAAN_SEWA,
            isi ="Mengajukan Permintaan Untuk Menyewa Kos",
            tanggal = tanggalHari,
        )

        database.child(Constant().PERMINTAAN)
            .push()
            .ref.setValue(permintaan)
            .addOnSuccessListener {

                Toast.makeText(this@DetailSewaKosActivity, "Sukses Mengajukan Permintaan Untuk Menyewa Kos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@DetailSewaKosActivity, "Gagal Mengajukan Permintaan Untuk Menyewa Kos", Toast.LENGTH_SHORT).show()
            }
    }





    private fun checkSewaKosCallback(sewaKosCallback: SewaKosCallback)
    {

        database.child(Constant().PERMINTAAN)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {snap->
                        val emailDari=snap.child(Constant().DARI).value.toString()
                        val idKos=snap.child(Constant().ID_KOS).value.toString()

                        if(emailDari==emailPengguna && idKos==kos.idKos)
                        {
                            sewaKosCallback.permintaanDitemukan(true)
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                   Log.d("Database Error",error.message)
                }

            })

        database.child(Constant().DAFTAR_SEWA_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->

                        val emailPenyewa=snap.child(Constant().KEY_EMAIL).value.toString()
                        val idKos=snap.child(Constant().ID_KOS).value.toString()

                        if(emailPenyewa==emailPengguna && idKos==kos.idKos)
                        {
                            sewaKosCallback.kosSudahDisewa(true)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }


    private fun setGambarKos(setImageListCallback: SetImageListCallback)
    {
        kos.gambarKos.indices.forEachIndexed { _, i ->
            storage.child(kos.gambarKos[i])
                .downloadUrl
                .addOnSuccessListener {uri->

                    slideArrayList.add(SlideModel(uri.toString(), ScaleTypes.FIT))
                    setImageListCallback.setImageList(slideArrayList)

                }
                .addOnFailureListener {
                    Toast.makeText(this@DetailSewaKosActivity, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun setDataKos()
    {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2

        binding.includeLayoutDetail.lblnamakos.text= kos.nama
        binding.includeLayoutDetail.lblfasilitas.text= kos.fasilitas
        binding.includeLayoutDetail.lblhargakos.text=  format.format(kos.biaya)
        binding.includeLayoutDetail.lbljenispembayaran.text=kos.jenisBayar
        binding.includeLayoutDetail.lbljeniskos.text=kos.jenis
        binding.includeLayoutDetail.lbldeskripsikos.text=kos.deskripsi

    }

}