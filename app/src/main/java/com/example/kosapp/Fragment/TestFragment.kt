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
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Callback.TestCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityRouteJalanBinding
import com.example.kosapp.databinding.FragmentSemuaKosBinding
import com.example.kosapp.databinding.FragmentTestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class TestFragment : Fragment(),HomeKosAdapter.ItemOnClick {

//    private lateinit var binding: FragmentTestBinding
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var homeKosAdapter: HomeKosAdapter
    private lateinit var kos:Kos

    private  var database=Firebase.database.reference
    private var kosArrayList=ArrayList<Kos>()

    private val binding by lazy{
        FragmentTestBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )=binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onClick(v: View, dataKos: Kos) {
        TODO("Not yet implemented")
    }

    fun showText()
    {
        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    kosArrayList.clear()

                    snapshot.children.forEach { snap->
                        kos=Kos(
                            idKos=snap.child(Constant().KEY_ID_KOS).value.toString(),
                            alamat = snap.child(Constant().KEY_ALAMAT_KOS).value.toString(),
                            biaya = snap.child(Constant().KEY_BIAYA_KOS).value.toString().toDouble(),
                            emailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString(),
                            gambarKos = snap.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>,
                            thumbnailKos = snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString(),
                            jenis=snap.child(Constant().KEY_JENIS_KOS).value.toString(),
                            jenisBayar = snap.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString(),
                            lattitude = snap.child(Constant().KEY_LATTITUDE_KOS).value.toString(),
                            longitude = snap.child(Constant().KEY_LONGITUDE_KOS).value.toString(),
                            nama = snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                            sisa = snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString().toInt(),
                            fasilitas=snap.child(Constant().KEY_FASILITAS).value.toString(),
                            deskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString(),
                        )
                        kosArrayList.add(kos)

                    }

                    homeKosAdapter= HomeKosAdapter(kosArrayList,this@TestFragment)
                    binding.rvtest.layoutManager=LinearLayoutManager(activity)
                    binding.rvtest.adapter=homeKosAdapter

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }



}