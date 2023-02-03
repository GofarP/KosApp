package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailKosSayaActivity
import com.example.kosapp.Activity.DetailSewaKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter.*
import com.example.kosapp.Callback.DaftarMenyewaCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Sewa
import com.example.kosapp.databinding.FragmentMenyewaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MenyewaFragment : Fragment(), ItemOnCLickMenyewa {

    private lateinit var  binding:FragmentMenyewaBinding
    private var sewaArrayList=ArrayList<Sewa>()
    private var kosArrayList=ArrayList<Kos>()
    private lateinit var adapter: MenyewaAdapter
    private var database=Firebase.database.reference
    val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var sewa:Sewa
    private lateinit var layoutManager:RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMenyewaBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data()
//        getDataMenyewa(object: DaftarMenyewaCallback{
//            override fun getDataMenyewa(arrayListMenyewa: ArrayList<Sewa>) {
//                database.child(Constant().DAFTAR_KOS)
//                    .addValueEventListener(object: ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.children.forEach { snap->
//                                snap.children.forEach {snapValue->
//                                   if(arrayListMenyewa[snapValue)
//                                }
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
//            }
//
//        })


    }

    private fun data()
    {
        database.child(Constant().DAFTAR_SEWA_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->

                        val snapEmail=snap.child(Constant().KEY_EMAIL).value.toString()
                        val snapIdKosSewa=snap.child(Constant().ID_KOS).value.toString()

                        if(emailPengguna==snapEmail)
                        {
                            database.child(Constant().DAFTAR_KOS)
                                .addValueEventListener(object: ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach {snap->
                                            val snapIdKos=snap.child(Constant().ID_KOS).value.toString()

                                            if(snapIdKos==snapIdKosSewa)
                                            {
                                                val kos=Kos(
                                                    idKos=snap.child(Constant().ID_KOS).value.toString(),
                                                    alamat = snap.child(Constant().ALAMAT_KOS).value.toString(),
                                                    biaya = snap.child(Constant().BIAYA_KOS).value.toString().toDouble(),
                                                    emailPemilik=snap.child(Constant().EMAIL_PEMILIK).value.toString(),
                                                    gambarKos = snap.child(Constant().GAMBAR_KOS).value as ArrayList<String>,
                                                    gambarThumbnail = snap.child(Constant().GAMBAR_THUMBNAIL_KOS).value.toString(),
                                                    jenis=snap.child(Constant().JENIS_KOS).value.toString(),
                                                    jenisBayar = snap.child(Constant().JENIS_BAYAR_KOS).value.toString(),
                                                    lattitude = snap.child(Constant().LATTITUDE_KOS).value.toString(),
                                                    longitude = snap.child(Constant().LONGITUDE_KOS).value.toString(),
                                                    nama = snap.child(Constant().NAMA_KOS).value.toString(),
                                                    sisa = snap.child(Constant().JUMLAH_KAMAR_KOS).value.toString().toInt(),
                                                    fasilitas=snap.child(Constant().FASILITAS).value.toString(),
                                                    deskripsi=snap.child(Constant().DESKRIPSI).value.toString(),
                                                )
                                                kosArrayList.add(kos)
                                            }

                                            adapter= MenyewaAdapter(kosArrayList,this@MenyewaFragment)
                                            layoutManager= LinearLayoutManager(activity)
                                            binding.rvmenyewa.layoutManager=layoutManager
                                            binding.rvmenyewa.adapter=adapter

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("error",error.message)
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }
//    private fun getDataMenyewa(daftarMenyewaCallback: DaftarMenyewaCallback)
//    {
//        database.child(Constant().DAFTAR_SEWA_KOS)
//            .addValueEventListener(object :ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.children.forEach { snap->
//                        val snapValue=snap.child(emailPengguna.replace(".",","))
//                        sewa=Sewa(
//                                idSewa = snapValue.child(Constant().ID_SEWA).value.toString(),
//                                email= snapValue.child(Constant().KEY_EMAIL).value.toString(),
//                                idKos = snapValue.child(Constant().ID_KOS).value.toString(),
//                                tanggal = snapValue.child(Constant().ID_SEWA).value.toString(),
//                            )
//
//                        sewaArrayList.add(sewa)
//                        daftarMenyewaCallback.getDataMenyewa(sewaArrayList)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//    }


//    private fun addData()
//    {
//        var kos= Kos(
//            id="12345",
//            nama = "Kos Jaya Makmur",
//            alamat="Jl. Jalan",
//            sisa=3,
//            jenis="Laki-Laki",
//            gambarThumbnail = "hehe",
//            gambarKos = arrayListOf("hehe","hihihi","huhuhu"),
//            biaya=300000.00,
//            lattitude = "",
//            longitude = "",
//            jenisBayar = "",
//            fasilitas="",
//            deskripsi="",
//        )
//
//        kosArrayList.add(kos)
//
//        kos= Kos(
//            id="21345",
//            nama="Kos Strong n independent",
//            jenis = "Perempuan",
//            alamat = "Jl.kaki",
//            sisa=3,
//            gambarThumbnail = "hihi",
//            gambarKos =  arrayListOf("hehe","hihihi","huhuhu"),
//            biaya = 200000.00,
//            lattitude = "",
//            longitude = "",
//            jenisBayar = "",
//            fasilitas="",
//            deskripsi="",
//        )
//
//        kosArrayList.add(kos)
//
//        kos= Kos(
//            id="321292812",
//            nama="Kost Mandiri",
//            alamat = "Jl.Kemana",
//            sisa=3,
//            jenis = "Laki-Laki",
//            gambarThumbnail = "huhahuha",
//            gambarKos = arrayListOf("hihi","hehe","haha"),
//            biaya=100000.00,
//            lattitude = "",
//            longitude = "",
//            jenisBayar = "",
//            fasilitas="",
//            deskripsi="",
//        )
//
//        kosArrayList.add(kos)
//    }

    override fun OnSelengkapnyaClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, DetailKosSayaActivity::class.java).putExtra("dataKos", dataKos)
        startActivity(intent)
    }



}