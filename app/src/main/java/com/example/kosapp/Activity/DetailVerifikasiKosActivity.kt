package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailVerifikasiAkunBinding
import com.example.kosapp.databinding.ActivityDetailVerifikasiKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Value

class DetailVerifikasiKosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVerifikasiKosBinding
    private lateinit var dataKosIntent: Intent
    private lateinit var permintaanVerifikasi: PermintaanVerifikasi


    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailVerifikasiKosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataKosIntent=intent

        permintaanVerifikasi=dataKosIntent.getParcelableExtra(Constant().KEY_PERMINTAAN_VERIFIKASI_KOS)!!


        getDataVerifikasiKos()


    }


    private fun getDataVerifikasiKos()
    {
        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasi.id)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val snapNamaKos=snapshot.child(Constant().KEY_NAMA_KOS).value.toString()
                    val snapAlamatKos=snapshot.child(Constant().KEY_ALAMAT_KOS).value.toString()
                    val snapEmailPemilik=snapshot.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                    val snapHargaKos=snapshot.child(Constant().KEY_BIAYA_KOS).value.toString()
                    val snapJenisKos=snapshot.child(Constant().KEY_BIAYA_KOS).value.toString()
                    val snapDeskripsiKos=snapshot.child(Constant().KEY_DESKRIPSI).value.toString()
                    val snapFasilitasKos=snapshot.child(Constant().KEY_FASILITAS).value.toString()

                    binding.lblnamakos.text=snapNamaKos
                    binding.lblalamatkos.text=snapAlamatKos
                    binding.lblemailpemilik.text=snapEmailPemilik
                    binding.lblhargakos.text=snapHargaKos
                    binding.lbljeniskos.text=snapJenisKos
                    binding.lbldeskripsikos.text=snapDeskripsiKos
                    binding.lblfasilitaskos.text=snapFasilitasKos


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }
}