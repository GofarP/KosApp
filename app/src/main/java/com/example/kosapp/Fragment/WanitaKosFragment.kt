package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
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
import com.example.kosapp.databinding.FragmentWanitaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class WanitaKosFragment : Fragment(), ItemOnClick {

    private var kosArrayList=ArrayList<Kos>()
    private lateinit var binding: FragmentWanitaBinding
    private lateinit var kos:Kos
    private var adapter:HomeKosAdapter?=null
    private var auth=FirebaseAuth.getInstance().currentUser
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var database=Firebase.database.reference
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentWanitaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(view.context)

        adapter=HomeKosAdapter(kosArrayList,this)
        val linearLayoutManager=LinearLayoutManager(activity)
        binding.rvkoswanita.layoutManager=linearLayoutManager
        binding.rvkoswanita.adapter=adapter
    }


    private fun getData()
    {

        database.child("daftarKos")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvkoswanita.adapter=null

                    snapshot.children.forEach { snap->


                            if(snap.child("jenis").value.toString() != "Wanita")
                            {
                                return@forEach
                            }

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

                    adapter= HomeKosAdapter(kosArrayList,this@WanitaKosFragment)
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvkoswanita.layoutManager=layoutManager
                    binding.rvkoswanita.adapter=adapter

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

        else if(dataKos.jenis!=jenisKelaminUser)
        {
            Toast.makeText(activity, "Jenis Kelamin Anda Tidak Cocok Untuk Kos Ini", Toast.LENGTH_SHORT).show()
        }

        else
        {
            val intent=Intent(activity, DetailSewaKosActivity::class.java).putExtra("dataKos", dataKos)
            startActivity(intent)
        }

    }


}