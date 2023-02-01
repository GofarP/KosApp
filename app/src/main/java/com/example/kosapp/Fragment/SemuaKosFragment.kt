package com.example.kosapp.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailSewaKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentSemuaKosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SemuaKosFragment : Fragment(), ItemOnClick {

    private lateinit var binding:FragmentSemuaKosBinding
    private  var kosArrayList=ArrayList<Kos>()
    private var homeKosAdapter:HomeKosAdapter?=null
    private var database=Firebase.database.reference
    private var auth=FirebaseAuth.getInstance().currentUser
    private lateinit var kos:Kos
    private lateinit var  layoutManager: RecyclerView.LayoutManager
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSemuaKosBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getData()

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(view.context)

        homeKosAdapter= HomeKosAdapter(kosArrayList, this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvkossemua.layoutManager=layoutManager
        binding.rvkossemua.adapter=homeKosAdapter

    }


    private fun getData()
    {

        database.child(Constant().DAFTAR_KOS)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvkossemua.adapter=null

                    snapshot.children.forEach { snap->
                       snap.children.forEach { snap->

                           kos=Kos(
                               id=snap.child("id").value.toString(),
                               alamat = snap.child("alamat").value.toString(),
                               biaya = snap.child("biaya").value.toString().toDouble(),
                               emailPemilik=snap.child("emailPemilik").value.toString(),
                               gambarKos = snap.child("gambarKos").value as ArrayList<String>,
                               gambarThumbnail = snap.child("gambarThumbnail").value.toString(),
                               jenis=snap.child("jenis").value.toString(),
                               jenisBayar = snap.child("jenisBayar").value.toString(),
                               lattitude = snap.child("lattitude").value.toString(),
                               longitude = snap.child("longitude").value.toString(),
                               nama = snap.child("nama").value.toString(),
                               sisa = snap.child("sisa").value.toString().toInt(),
                               fasilitas=snap.child("fasilitas").value.toString(),
                               deskripsi=snap.child("deskripsi").value.toString(),
                           )

                           kosArrayList.add(kos)
                       }
                    }

                    homeKosAdapter= HomeKosAdapter(kosArrayList,this@SemuaKosFragment)
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvkossemua.layoutManager=layoutManager
                    binding.rvkossemua.adapter=homeKosAdapter

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }



    override fun onClick(v: View, dataKos: Kos) {

        val jenisKelaminUser=preferenceManager.getString(Constant().KEY_JENIS_KELAMIN)

        if(dataKos.sisa==0)
        {
            Toast.makeText(activity, "Mohon Maaf, Kos Sedang Penuh", Toast.LENGTH_SHORT).show()
        }

        else if(dataKos.jenis != jenisKelaminUser && dataKos.jenis!="Campur")
        {
            Toast.makeText(activity, "Jenis Kelamin Anda Tidak Cocok Untuk Kos Ini $jenisKelaminUser", Toast.LENGTH_SHORT).show()
        }

        else
        {
            val intent=Intent(activity, DetailSewaKosActivity::class.java).putExtra("dataKos", dataKos)
            startActivity(intent)
        }

    }

}