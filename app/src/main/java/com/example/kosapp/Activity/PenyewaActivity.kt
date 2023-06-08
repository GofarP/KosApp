package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter.*
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.Model.RatingProfile
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityPenyewaBinding
import com.example.kosapp.databinding.LayoutBeriRatingProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PenyewaActivity : AppCompatActivity(), PenggunaItemOnCLick {

    private var penyewaArrayList=ArrayList<Pengguna>()
    private var database= Firebase.database.reference
    private val VIEW_PENGGUNA=2
    private val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var calendar= Calendar.getInstance()
    private var sudahDiberiRating=false

    private lateinit var binding:ActivityPenyewaBinding
    private lateinit var adapter: PenyewaAdapter
    private lateinit var dataKosIntent: Intent
    private lateinit var kos:Kos
    private lateinit var pengguna: Pengguna
    private lateinit var  layoutManager: RecyclerView.LayoutManager
    private lateinit var  transaksi:Transaksi
    private lateinit var tanggalHariIni:String
    private lateinit var ratingProfile:RatingProfile
    private lateinit var ratingProfileMap:MutableMap<String,Any>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPenyewaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeLayoutDetail.titleactionbar.text="Daftar Penyewa"

        Helper().setStatusBarColor(this@PenyewaActivity)

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!



        getData()

    }


    fun getData()
    {
        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    penyewaArrayList.clear()
                    snapshot.children.forEach { snapSewa->

                        val snapSewaIdKos=snapSewa.child(Constant().KEY_ID_KOS).value.toString()
                        val snapSewaEmail=snapSewa.child(Constant().KEY_EMAIL).value.toString()

                        if(snapSewaIdKos==kos.idKos)
                        {
                            database.child(Constant().KEY_USER)
                                .addValueEventListener(object:ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach { snapUser->

                                            val snapUserEmail=snapUser.child(Constant().KEY_EMAIL).value.toString()

                                            if(snapUserEmail==snapSewaEmail)
                                            {
                                                pengguna= Pengguna(
                                                    id=snapUser.child(Constant().KEY_ID_PENGGUNA).value.toString(),
                                                    email=snapUser.child(Constant().KEY_EMAIL).value.toString(),
                                                    foto=snapUser.child(Constant().KEY_FOTO).value.toString(),
                                                    jenisKelamin = snapUser.child(Constant().KEY_JENIS_KELAMIN).value.toString(),
                                                    noTelp=snapUser.child(Constant().KEY_NOTELP).value.toString(),
                                                    username = snapUser.child(Constant().KEY_USERNAME).value.toString(),
                                                    nik=snapUser.child(Constant().KEY_NIK).value.toString(),
                                                    kelurahan=snapUser.child(Constant().KEY_KELURAHAN).value.toString(),
                                                    kecamatan=snapUser.child(Constant().KEY_KECAMATAN).value.toString()
                                                )

                                                penyewaArrayList.add(pengguna)
                                            }
                                        }

                                        adapter= PenyewaAdapter(penyewaArrayList, VIEW_PENGGUNA)
                                        adapter.penggunaClickListener=this@PenyewaActivity
                                        layoutManager=LinearLayoutManager(this@PenyewaActivity)
                                        binding.rvpenyewa.layoutManager=layoutManager
                                        binding.rvpenyewa.adapter=adapter

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("error db",error.message)
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error", error.message)
                }

            })
    }


    override fun OnClickDetail(view: View, pengguna: Pengguna) {

        val dialogView=layoutInflater.inflate(R.layout.layout_beri_rating_profile, null)
        val customDialog=AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        val customDialogBinding= LayoutBeriRatingProfileBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        val arrayRatingPengguna=arrayOf(Constant().KEY_RATING_SANGAT_BAIK,
            Constant().KEY_RATING_BAIK, Constant().KEY_RATING_BIASA,
            Constant().KEY_RATING_BURUK, Constant().KEY_RATING_SANGAT_BURUK
        )


        val ratingProfileAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayRatingPengguna)
        ratingProfileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        customDialogBinding.spnrating.adapter=ratingProfileAdapter

        
        database.child(Constant().KEY_RATING_USER)
            .child(pengguna.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists())
                {
                    val snapRating=snapshot.child(kos.idKos).child(Constant().KEY_RATING_USER).value.toString()
                    val jenisRatingProfilePosition=ratingProfileAdapter.getPosition(snapRating)
                    customDialogBinding.spnrating.setSelection(jenisRatingProfilePosition)
                    sudahDiberiRating=true
                    customDialogBinding.btntambahrating.text="Update Rating"
                    customDialogBinding.lbltitleratingprofile.text="Update Rating"
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db error", "Database Error")
            }
        })

        ratingProfile=RatingProfile(
            idKos = kos.idKos,
            idPengguna = pengguna.id,
            namaKos = kos.nama,
            ratingPengguna = customDialogBinding.spnrating.selectedItem.toString(),
            tanggal ="20-02-1999"
        )

        customDialogBinding.btntambahrating.setOnClickListener {

            if(sudahDiberiRating)
            {
                ratingProfileMap=HashMap()
                ratingProfileMap[Constant().KEY_RATING_USER]=customDialogBinding.spnrating.selectedItem.toString()
                ratingProfileMap[Constant().KEY_TANGGAL]=tanggalHariIni
                database.child(Constant().KEY_RATING_USER)
                    .child(pengguna.id)
                    .child(kos.idKos)
                    .updateChildren(ratingProfileMap)
                    .addOnSuccessListener {
                        Toast.makeText(this@PenyewaActivity, "Sukses Mengubah Rating Profile Pengguna", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@PenyewaActivity, "Gagal Mengubah Rating Profile Pengguna", Toast.LENGTH_SHORT).show()
                    }
            }

            else
            {
                database.child(Constant().KEY_RATING_USER)
                    .child(pengguna.id)
                    .child(kos.idKos)
                    .setValue(ratingProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this@PenyewaActivity, "Sukses Menambah Rating Profile Pengguna ", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@PenyewaActivity, "Gagal Menambah Rating Profile Pengguna", Toast.LENGTH_SHORT).show()
                    }
            }

            customDialog.dismiss()

        }


    }

    override fun OnClickDelete(view: View, pengguna: Pengguna) {

        val alertBuilder=AlertDialog.Builder(this@PenyewaActivity)
        alertBuilder.setMessage("Apakah Yakin Ingin Mengeluarkan Pengguna ${pengguna.username} dengan email ${pengguna.email}?")
            .setCancelable(true)
            .setPositiveButton("Ya"){dialog, id->
                database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                    .addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {snap->
                                val snapEmail=snap.child(Constant().KEY_EMAIL).value.toString()
                                val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                                val snapKey=snap.key

                                if(snapEmail==pengguna.email && snapIdKos==kos.idKos)
                                {
                                    database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                                        .child(snapKey.toString())
                                        .removeValue()
                                        .addOnSuccessListener {

                                            transaksi= Transaksi(
                                                transaksiId = UUID.randomUUID().toString(),
                                                dari=emailPengguna,
                                                kepada=snapEmail,
                                                judul="Pengeluaran Kos",
                                                isi = "Anda Dikeluarkan Dari Kos ${kos.nama} oleh pemilik",
                                                tipe = Constant().KEY_PENGELUARAN_KOS,
                                                tanggal =tanggalHariIni
                                            )

                                            database.child(Constant().KEY_TRANSAKSI)
                                                .push()
                                                .setValue(transaksi)

                                            database.child(Constant().KEY_DAFTAR_KOS)
                                                .child(kos.idKos)
                                                .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                                .setValue(ServerValue.increment(1))


                                            val indexPenyewa=penyewaArrayList.indexOf(pengguna)
                                            adapter.notifyItemRemoved(indexPenyewa)

                                            Toast.makeText(this@PenyewaActivity, "Sukses Mengeluarkan Penyewa", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("db error", error.message)
                        }

                    })
            }
            .setNegativeButton("Tidak"){dialog, id->

            }

        val alert=alertBuilder.create()
        alert.show()
    }


}