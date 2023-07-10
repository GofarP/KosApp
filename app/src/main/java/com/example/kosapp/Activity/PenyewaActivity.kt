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
import com.example.kosapp.Model.*
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
    private val idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
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
    private lateinit var history: History


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPenyewaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeLayoutDetail.titleactionbar.text="Daftar Penyewa"

        Helper().setStatusBarColor(this@PenyewaActivity)

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra(Constant().KEY_DATA_KOS)!!



        getData()

    }


    private fun getData()
    {
        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    penyewaArrayList.clear()
                    snapshot.children.forEach {snapPenyewa->
                        snapPenyewa.children.forEach {snapKos->
                            val snapSewaIdKos=snapKos.child(Constant().KEY_ID_KOS).value.toString()
                            val snapIdPenyewa=snapKos.child(Constant().KEY_ID_PENYEWA).value.toString()

                            if(snapSewaIdKos==kos.idKos)
                            {
                                database.child(Constant().KEY_USER)
                                    .child(snapIdPenyewa)
                                    .get().addOnSuccessListener {snapUser->

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

                                        adapter= PenyewaAdapter(penyewaArrayList, VIEW_PENGGUNA)
                                        adapter.penggunaClickListener=this@PenyewaActivity
                                        layoutManager=LinearLayoutManager(this@PenyewaActivity)
                                        binding.rvpenyewa.layoutManager=layoutManager
                                        binding.rvpenyewa.adapter=adapter
                                    }


                            }


                        }
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
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
            .child(kos.idKos)
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
            namaKos = kos.namaKos,
            ratingPengguna = customDialogBinding.spnrating.selectedItem.toString(),
            tanggal =tanggalHariIni
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
                    .child(pengguna.id)
                    .child(kos.idKos)
                    .removeValue()
                    .addOnSuccessListener {

                        transaksi= Transaksi(
                            idTransaksi = UUID.randomUUID().toString(),
                            idPenyewa = pengguna.id,
                            idPemilik = idPengguna,
                            judul="Pengeluaran Kos",
                            isi = "Anda Dikeluarkan Dari Kos ${kos.namaKos} oleh pemilik",
                            tipe = Constant().KEY_PENGELUARAN_KOS,
                            tanggal =tanggalHariIni
                        )

                        database.child(Constant().KEY_TRANSAKSI)
                            .child(pengguna.id)
                            .push()
                            .setValue(transaksi)

                        transaksi.isi="Anda Mengeluarkan ${pengguna.email} Dari Kos Anda"

                        database.child(Constant().KEY_TRANSAKSI)
                            .child(kos.idPemilik)
                            .push()
                            .setValue(transaksi)

                        database.child(Constant().KEY_DAFTAR_KOS)
                            .child(kos.idKos)
                            .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                            .setValue(ServerValue.increment(1))


                        history=History(
                            idHistory = UUID.randomUUID().toString(),
                            alamat=kos.alamat,
                            idKos = kos.idKos,
                            namaKos = kos.namaKos,
                            tanggal = tanggalHariIni,
                            thumbnailKos = kos.thumbnailKos
                        )

                        database.child(Constant().KEY_HISTORY)
                            .child(pengguna.id)
                            .push()
                            .setValue(history)


                        val indexPenyewa=penyewaArrayList.indexOf(pengguna)
                        adapter.notifyItemRemoved(indexPenyewa)

                        Toast.makeText(this@PenyewaActivity, "Sukses Mengeluarkan Penyewa", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@PenyewaActivity, "Gagal Mengeluarkan Penyewas", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Tidak"){dialog, id->

            }

        val alert=alertBuilder.create()
        alert.show()
    }


}