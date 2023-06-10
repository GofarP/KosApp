package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.SetImageListCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailKosSayaBinding
import com.example.kosapp.databinding.LayoutRatingBinding
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

    private lateinit var permintaan:Permintaan
    private lateinit var kos: Kos
    private lateinit var dataKosIntent: Intent
    private lateinit var binding:ActivityDetailKosSayaBinding
    private lateinit var tglHariIni:String

    private val slideArrayList=ArrayList<SlideModel>()
    private val storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private var calendar=Calendar.getInstance()
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var hashMapAddRating=HashMap<String, Any>()
    private var hashMapKosRating= mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0)
    private var jumlahRating=0
    private var totalRating=0
    private var nilaiAkhirRating=0

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

        binding.btnchatpemilik.setOnClickListener {
            val intent=Intent(this@DetailKosSayaActivity, ChatActiviity::class.java)
            intent.putExtra(Constant().KEY_EMAIL_PENGIRIM,emailPengguna)
            intent.putExtra(Constant().KEY_EMAIL_PENGIRIM,kos.idPemilik)

            startActivity(intent)
        }

        binding.btnaddcomment.setOnClickListener {
            val intent=Intent(this@DetailKosSayaActivity, CommentActivity::class.java)
            intent.putExtra(Constant().KEY_ID_KOS,kos.idKos)
            intent.putExtra(Constant().KEY_EMAIL_PEMILIK,kos.idPemilik)
            startActivity(intent)
        }

        binding.btnrating.setOnClickListener {
            beriRating()
        }


    }

    private fun setDataKos()
    {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2

        binding.includeLayoutDetail.lblnamakos.text= kos.namaKos
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
            idPenyewa = idPengguna,
            idPemilik=kos.idPemilik,
            emailPenyewa=emailPengguna,
            emailPemilik=kos.emailPemilik,
            namaKos=kos.namaKos,
            judul = Constant().PERMINTAAAN_AKHIRI_SEWA,
            isi ="Mengajukan Permintaan Untuk Mengakhiri Sewa Kos ${kos.namaKos}",
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


    private fun beriRating()
    {

        val dialogView=layoutInflater.inflate(R.layout.layout_rating, null)
        val customDialog=AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        val customDialogBinding= LayoutRatingBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        database.child(Constant().KEY_RATING)
            .child(kos.idKos)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   snapshot.children.forEach {snap->
                       val snapIdPengguna=snap.child(Constant().KEY_ID_PENGGUNA).value.toString()
                       val snapRating=snap.child(Constant().KEY_RATING).value.toString().toInt()

                       if(snapIdPengguna==idPengguna)
                       {
                            customDialogBinding.ratingbarkos.rating= snapRating.toFloat()
                       }

                       jumlahRating=hashMapKosRating[snapRating]?:0
                       hashMapKosRating[snapRating]=jumlahRating+1

                   }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error", error.message)
                }
            })

        customDialogBinding.btnaddrating.setOnClickListener {

            val rating=customDialogBinding.ratingbarkos.rating.toString()

            jumlahRating=hashMapKosRating[rating.toInt()]?:0
            hashMapKosRating[rating.toInt()]=jumlahRating+1

            hashMapAddRating[Constant().KEY_ID_PENGGUNA]=idPengguna
            hashMapAddRating[Constant().KEY_RATING]=rating.toDouble()

            for(ratingKey in hashMapKosRating.keys)
            {
                jumlahRating+=ratingKey * hashMapKosRating[ratingKey]!!
                totalRating+=hashMapKosRating[ratingKey]!!
            }

            nilaiAkhirRating=(jumlahRating.toFloat() / totalRating.toFloat()).toInt()

            database.child(Constant().KEY_DATA_KOS)
                .child(kos.idKos)
                .child(Constant().KEY_RATING)
                .setValue(nilaiAkhirRating)

            database.child(Constant().KEY_RATING)
                .child(kos.idKos)
                .push()
                .setValue(hashMapAddRating)
                .addOnSuccessListener {
                    Toast.makeText(this@DetailKosSayaActivity, "Sukses Menambah Rating", Toast.LENGTH_SHORT).show()
                }

        }

    }
}