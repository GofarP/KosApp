package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailVerifikasiAkunBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class DetailVerifikasiAkunActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailVerifikasiAkunBinding
    private lateinit var dataAkunIntent: Intent
    private lateinit var permintaanVerifikasi: PermintaanVerifikasi

    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailVerifikasiAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@DetailVerifikasiAkunActivity)

        dataAkunIntent=intent

        permintaanVerifikasi=dataAkunIntent.getParcelableExtra(Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN)!!

        binding.lbldetailidverifikasi.text=permintaanVerifikasi.idPermintaan
        binding.lbldetailidpengguna.text=permintaanVerifikasi.id
        binding.lbldetailnama.text=permintaanVerifikasi.username
        binding.lbldetailemail.text=permintaanVerifikasi.email

        database.child(Constant().KEY_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val snapFoto=snapshot.child(Constant().KEY_FOTO).value.toString()
                    
                    storage.child(snapFoto)
                        .downloadUrl
                        .addOnSuccessListener { uri->
                            Glide.with(this@DetailVerifikasiAkunActivity)
                                .load(uri)
                                .into(binding.ivdetailverifikasi)
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "Gagal Mengambil Foto Penguna", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}