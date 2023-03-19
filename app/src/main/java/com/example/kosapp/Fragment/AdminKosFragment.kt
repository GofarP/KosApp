package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailVerifikasiAkunActivity
import com.example.kosapp.Activity.DetailVerifikasiKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentAdminKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class FragmentAdminKos : Fragment(), ItemOnClick {

    private lateinit var kos:Kos
    private lateinit var binding:FragmentAdminKosBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter:HomeKosAdapter

    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference
    private var kosArrayList=ArrayList<Kos>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAdminKosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataKos()
    }


    private fun getDataKos()
    {
        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    kosArrayList.clear()
                    binding.rvaadminkos.adapter=null

                    snapshot.children.forEach {snap->
                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                        val snapBiaya=snap.child(Constant().KEY_BIAYA_KOS).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapGambarKos=snap.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>
                        val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()
                        val snapJenis=snap.child(Constant().KEY_JENIS_KOS).value.toString()
                        val snapJenisBayar=snap.child(Constant().KEY_JENIS_KELAMIN).value.toString()
                        val snapLattitude=snap.child(Constant().KEY_LATTITUDE_KOS).value.toString()
                        val snapLongitude=snap.child(Constant().KEY_LONGITUDE_KOS).value.toString()
                        val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        val snapSisa=snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString()
                        val snapFasilitas=snap.child(Constant().KEY_FASILITAS).value.toString()
                        val snapDeskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString()
                        val snapStatus=snap.child(Constant().KEY_STATUS_VERIFIKASI_KOS).value.toString()

                        if(snapStatus==Constant().KEY_TERVERIFIKASI)
                        {
                            kos=Kos(
                                idKos=snapIdKos,
                                alamat = snapAlamat,
                                biaya = snapBiaya.toDouble(),
                                emailPemilik=snapEmailPemilik,
                                gambarKos = snapGambarKos,
                                thumbnailKos = snapThumbnailKos,
                                jenis=snapJenis,
                                jenisBayar = snapJenisBayar,
                                lattitude = snapLattitude,
                                longitude = snapLongitude,
                                nama = snapNamaKos,
                                sisa = snapSisa.toInt(),
                                fasilitas=snapFasilitas,
                                deskripsi=snapDeskripsi,
                                status=snapStatus

                            )

                            kosArrayList.add(kos)
                            adapter= HomeKosAdapter(kosArrayList,this@FragmentAdminKos)
                            linearLayoutManager=LinearLayoutManager(activity)
                            binding.rvaadminkos.layoutManager=linearLayoutManager
                            binding.rvaadminkos.adapter=adapter
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }

    override fun onClick(v: View, dataKos: Kos) {
        startActivity(Intent(activity,DetailVerifikasiKosActivity::class.java))
    }


}