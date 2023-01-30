package com.example.kosapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.ActivityDetailSewaKosBinding
import com.example.kosapp.databinding.LayoutKonfirmasiBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat

class DetailSewaKosActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailSewaKosBinding
    private var slideArrayList=ArrayList<SlideModel>()
    private var storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private var emailUser=Firebase.auth.currentUser?.email
    private lateinit var dataKosIntent:Intent
    private lateinit var kos:Kos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailSewaKosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@DetailSewaKosActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!
        setDataKosan()

        binding.btnaddcomment.setOnClickListener {
            startActivity(Intent(this@DetailSewaKosActivity,CommentActivity::class.java))
        }

        binding.btnpesan.setOnClickListener {
            val binding=LayoutKonfirmasiBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnkembali.setOnClickListener {
                startActivity(Intent(this@DetailSewaKosActivity, MainActivity::class.java))
            }

        }

    }

    private fun sewaKos()
    {

    }

    fun cekIdSewaKos():Boolean
    {
        database.child(Constant().PERMINTAAN)
            .child(emailUser.toString())
            .orderByChild(Constant().ID_KOS)
            .equalTo(kos.id)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun setDataKosan()
    {
        val intent = intent
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2

        binding.includeLayoutDetail.lblnamakos.text= kos.nama
        binding.includeLayoutDetail.lblfasilitas.text= kos.fasilitas
        binding.includeLayoutDetail.lblhargakos.text=  format.format(kos.biaya)
        binding.includeLayoutDetail.lbljenispembayaran.text=kos.jenisBayar
        binding.includeLayoutDetail.lbljeniskos.text=kos.jenis
        binding.includeLayoutDetail.lbldeskripsikos.text=kos.deskripsi

        for(i in kos.gambarKos.indices)
        {
            storage.child(kos.gambarKos[i])
                .downloadUrl
                .addOnSuccessListener {uri->
                    Log.d("uri",i.toString())

                    slideArrayList.add(SlideModel(uri.toString(), ScaleTypes.FIT))

                    if(i == kos.gambarKos.size-1 )
                    {
                        binding.includeLayoutDetail.sliderDetailKos.setImageList(slideArrayList)
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this@DetailSewaKosActivity, it.message, Toast.LENGTH_SHORT).show()
                }

        }

    }





}