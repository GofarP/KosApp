package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.widget.Toast
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.SetImageListCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailKosSayaBinding
import com.google.firebase.auth.FirebaseAuth
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
import kotlin.collections.HashMap

class DetailKosSayaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailKosSayaBinding
    private val slideArrayList=ArrayList<SlideModel>()
    private val storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private lateinit var permintaan:Permintaan
    private lateinit var kos: Kos
    private lateinit var dataKosIntent: Intent
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var calendar=Calendar.getInstance()
    private lateinit var tglHariIni:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailKosSayaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@DetailKosSayaActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!

        tglHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        cekPermintaanKeluarKos()

        setDataKos()

        setGambarKos(object:SetImageListCallback{

            override fun setImageList(listGambarKos: ArrayList<SlideModel>) {
                binding.includeLayoutDetail.sliderDetailKos.setImageList(listGambarKos)
            }


        })

        binding.btncancel.setOnClickListener {
            keluarKos()
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
                    Toast.makeText(this@DetailKosSayaActivity, it.message, Toast.LENGTH_SHORT).show()
                }
        }

    }


    private fun cekPermintaanKeluarKos()
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->
                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapEmail=snap.child(Constant().KEY_DARI).value.toString()

                        if(snapIdKos==kos.idKos && emailPengguna==snapEmail)
                        {
                            binding.btncancel.isEnabled=false
                            binding.btncancel.text="Permintaan Diproses..."
                            binding.btncancel.setBackgroundResource(R.drawable.button_background_disabled)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   Log.d("db error",error.message)
                }

            })
    }

    private fun keluarKos()
    {
        permintaan= Permintaan(
            idPermintaan= UUID.randomUUID().toString(),
            idKos=kos.idKos,
            namaKos=kos.nama,
            dari = emailPengguna,
            kepada = kos.emailPemilik,
            judul = Constant().PERMINTAAAN_AKHIRI_SEWA,
            isi ="Mengajukan Permintaan Untuk Mengakhiri Sewa Kos ${kos.nama}",
            tanggal = tglHariIni,
        )

        database.child(Constant().KEY_PERMINTAAN)
            .push()
            .ref.setValue(permintaan)
            .addOnSuccessListener {

                binding.btncancel.isEnabled=false
                binding.btncancel.text="Permintaan Diproses..."
                binding.btncancel.setBackgroundResource(R.drawable.button_background_disabled)
                Toast.makeText(this@DetailKosSayaActivity, "Sukses Mengajukan Permintaan Untuk Mengakhiri Sewa Kos", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this@DetailKosSayaActivity, "Gagal Mengajukan Permintaan Untuk Mengakhiri Sewa Kos", Toast.LENGTH_SHORT).show()
            }


    }
}