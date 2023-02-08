package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.TransaksiAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.databinding.ActivityTransaksiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransaksiActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTransaksiBinding
    private  var transaksiList=ArrayList<Transaksi>()
    private lateinit var adapter:TransaksiAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager

    private var database=Firebase.database.reference
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var transaksi:Transaksi
    private var calendar=Calendar.getInstance()
    private lateinit var tglHariIni:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tglHariIni=SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        Helper().setStatusBarColor(this@TransaksiActivity)


        getDataHistory()
        adapter=TransaksiAdapter(transaksiList)
        val layoutManager:RecyclerView.LayoutManager=LinearLayoutManager(this)
        binding.rvhistory.layoutManager=layoutManager
        binding.rvhistory.adapter=adapter
    }

    private fun getDataHistory()
    {
        database.child(Constant().KEY_TRANSAKSI)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    transaksiList.clear()
                    binding.rvhistory.adapter=null

                    snapshot.children.forEach { snap->
                        val childDari=snap.child(Constant().KEY_DARI).value.toString()
                        val childKepada=snap.child(Constant().KEY_KEPADA).value.toString()

                        if(childDari==emailPengguna || childKepada==emailPengguna)
                        {
                            transaksi= Transaksi(
                                transaksiId=snap.child(Constant().KEY_ID_TRANSAKSI).value.toString(),
                                dari = childDari,
                                kepada = childKepada,
                                judul=snap.child(Constant().KEY_JUDUL).value.toString(),
                                isi = snap.child(Constant().KEY_ISI).value.toString(),
                                tipe = snap.child(Constant().KEY_TYPE).value.toString(),
                                tanggal = snap.child(Constant().KEY_TANGGAL).value.toString()
                            )

                            transaksiList.add(transaksi)
                        }

                        adapter=TransaksiAdapter(transaksiList)
                        layoutManager=LinearLayoutManager(this@TransaksiActivity)
                        binding.rvhistory.layoutManager=layoutManager
                        binding.rvhistory.adapter=adapter


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}