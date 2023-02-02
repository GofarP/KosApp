package com.example.kosapp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter.OnClickListener
import com.example.kosapp.Callback.PermintaanCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.Model.Sewa
import com.example.kosapp.databinding.FragmentPermintaanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class PermintaanFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentPermintaanBinding
    private lateinit var adapter:PermintaanAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var database=Firebase.database.reference
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var permintaanArrayList=ArrayList<Permintaan>()
    private lateinit var permintaan: Permintaan
    private lateinit var sewa: Sewa
    private lateinit var history: History

    private var pengirim=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPermintaanBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPermintaan(object: PermintaanCallback{
            override fun getData(arrayListPermintaan: ArrayList<Permintaan>) {
                adapter= PermintaanAdapter(arrayListPermintaan, this@PermintaanFragment)
                layoutManager=LinearLayoutManager(activity)
                binding.rvpermintaan.layoutManager=layoutManager
                binding.rvpermintaan.adapter=adapter
            }

        })

    }


    private fun getPermintaan(permintaanCallback: PermintaanCallback)
    {
        database.child(Constant().PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->

                            permintaanArrayList.clear()
                            binding.rvpermintaan.adapter=null

                            val emailChildKepada=snap.child(Constant().KEPADA).value.toString()
                            val emailChildDari=snap.child(Constant().DARI).value.toString()

                            if(emailChildDari == emailPengguna || emailChildKepada==emailPengguna)
                            {
                                permintaan=Permintaan(
                                    dari=snap.child(Constant().DARI).value.toString(),
                                    idKos=snap.child(Constant().ID_KOS).value.toString(),
                                    idPermintaan = snap.child(Constant().ID_PERMINTAAN).value.toString(),
                                    isi=snap.child(Constant().ISI).value.toString(),
                                    judul=snap.child(Constant().JUDUL).value.toString(),
                                    kepada = snap.child(Constant().KEPADA).value.toString(),
                                    namaKos = snap.child(Constant().NAMA_KOS).value.toString(),
                                    tanggal =  Date()
                                )
                                permintaanArrayList.add(permintaan)
                                permintaanCallback.getData(permintaanArrayList)
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }


    private fun terimaPermintaan(permintaanId: String)
    {
        database.child(Constant().PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().ID_PERMINTAAN).value.toString()==permintaanId)
                        {
                            snap.ref.removeValue()
                                .addOnSuccessListener {

                                    sewa=Sewa(
                                        idSewa = UUID.randomUUID().toString(),
                                        email=permintaan.dari
                                    )

                                    history=History(
                                        dari=emailPengguna,
                                        historyId = UUID.randomUUID().toString(),
                                        isi="Permintaan Sewa Diterima",
                                        judul="Permintaan Sewa",
                                        kepada=permintaan.dari,
                                        tanggal=Date(),
                                        tipe=Constant().TERIMA_SEWA
                                    )

                                    database.child(Constant().DAFTAR_SEWA_KOS)
                                        .child(permintaan.idKos)
                                        .child(permintaan.dari)
                                        .setValue(sewa)

                                    database.child(Constant().HISTORY)
                                        .push()
                                        .setValue(history)

//                                    database.child(Constant().DAFTAR_KOS)
//                                        .child(emailPengguna)
//                                        .child(Constant().JUMLAH_KAMAR_KOS)
//

                                    Toast.makeText(activity, "Sukses Menerima Permintaan", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener {error->
                                    Toast.makeText(activity, "Gagal Menerima Permintaan", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db",error.message)
                }

            })
    }

    private fun tolakPermintaan(permintaanId:String)
    {
        database.child(Constant().PERMINTAAN)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().ID_PERMINTAAN).value.toString()==permintaanId)
                        {
                           snap.ref.removeValue()
                               .addOnSuccessListener {

                                   val history=History(
                                       historyId=UUID.randomUUID().toString(),
                                       judul = "Permintaan Sewa Dibatalkan",
                                       isi = "Anda Membatalkan Permintaan Sewa",
                                       tipe=Constant().BATAL_SEWA,
                                       dari=emailPengguna,
                                       kepada=permintaan.dari,
                                       tanggal=Date()
                                   )
                                   database.child(Constant().HISTORY)
                                       .push()
                                       .setValue(history)

                                   Toast.makeText(activity, "Sukses Menolak Permintaan", Toast.LENGTH_SHORT).show()

                               }
                               .addOnFailureListener {
                                   Toast.makeText(activity, "Gagal Menolak Permintaan", Toast.LENGTH_SHORT).show()
                               }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database Error",error.message)
                }

            })
    }


    private fun batalkanPermintaan(idPermintaan: String)
    {
        database.child(Constant().PERMINTAAN)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->
                        if(snap.child(Constant().ID_PERMINTAAN).value.toString()==idPermintaan)
                        {
                            snap.ref.removeValue()
                                .addOnSuccessListener {

                                    val history=History(
                                        historyId=UUID.randomUUID().toString(),
                                        judul = "Permintaan Sewa Ditolak",
                                        isi = "Permintaan Sewa Kos Anda Ditolak",
                                        tipe="permintaan sewa",
                                        dari=emailPengguna,
                                        kepada=permintaan.dari,
                                        tanggal=Date()
                                    )

                                    database.child(Constant().HISTORY)
                                        .push()
                                        .setValue(history)

                                    Toast.makeText(activity, "Sukses Membatalkan Permintaan", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onTerimaCLickListener(view: View, dataPermintaan: Permintaan) {
        terimaPermintaan(dataPermintaan.idPermintaan)
    }

    override fun onTolakClickListener(view: View, dataPermintaan: Permintaan) {
        if(dataPermintaan.dari==emailPengguna)
        {
            batalkanPermintaan(dataPermintaan.idPermintaan)
        }

        else
        {
            tolakPermintaan(dataPermintaan.idPermintaan)
        }
    }


}