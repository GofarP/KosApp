package com.example.kosapp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.TransaksiAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.databinding.FragmentTransaksiAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TransaksiAdminFragment : Fragment() {

    private lateinit var  binding:FragmentTransaksiAdminBinding
    private lateinit var transaksi: Transaksi
    private lateinit var adapter:TransaksiAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private  val database=FirebaseDatabase.getInstance().reference
    private val idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var transaksiArrayList=ArrayList<Transaksi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTransaksiAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }


    private fun getData()
    {
        database.child(Constant().KEY_TRANSAKSI)
            .child(idPengguna)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    transaksiArrayList.clear()
                    binding.rvnotifikasiadmin.adapter=null

                    snapshot.children.forEach { snap->

                        snapshot.children.forEach { snap->
                            val childIdPenyewa=snap.child(Constant().KEY_ID_PEMILIK).value.toString()
                            val childIdPemilik=snap.child(Constant().KEY_ID_PENYEWA).value.toString()

                            if(childIdPemilik==idPengguna || childIdPenyewa==idPengguna)
                            {
                                transaksi= Transaksi(
                                    idTransaksi=snap.child(Constant().KEY_ID_TRANSAKSI).value.toString(),
                                    judul=snap.child(Constant().KEY_JUDUL).value.toString(),
                                    idPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString(),
                                    idPenyewa=snap.child(Constant().KEY_ID_PENYEWA).value.toString(),
                                    isi = snap.child(Constant().KEY_ISI).value.toString(),
                                    tipe = snap.child(Constant().KEY_TYPE).value.toString(),
                                    tanggal = snap.child(Constant().KEY_TANGGAL).value.toString()

                                )

                                transaksiArrayList.add(transaksi)
                            }

                            adapter=TransaksiAdapter(transaksiArrayList)
                            layoutManager=LinearLayoutManager(context)
                            binding.rvnotifikasiadmin.layoutManager=layoutManager
                            binding.rvnotifikasiadmin.adapter=adapter

                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB Error",error.message)
                }

            })
    }

}