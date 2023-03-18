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
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentCampurKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class CampurKosFragment : Fragment(), ItemOnClick {


    private lateinit var binding:FragmentCampurKosBinding
    private  var kosArrayList=ArrayList<Kos>()
    private lateinit var kos:Kos
    private var adapter:HomeKosAdapter?=null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var database= Firebase.database.reference
    private lateinit var jenisKos:String
    private lateinit var namaKos:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCampurKosBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataKosCampuran()
    }


    fun getDataKosCampuran()
    {

        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvkoscampur.adapter=null

                    snapshot.children.forEach { snap->

                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                        val snapBiaya=snap.child(Constant().KEY_BIAYA_KOS).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapGambarKos=snap.child(Constant().KEY_GAMBAR_KOS).value.toString() as ArrayList<String>
                        val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_KOS).value.toString()
                        val snapJenis=snap.child(Constant().KEY_JENIS_KOS).value.toString()
                        val snapJenisBayar=snap.child(Constant().KEY_JENIS_KELAMIN).value.toString()
                        val snapLattitude=snap.child(Constant().KEY_LATTITUDE_KOS).value.toString()
                        val snapLongitude=snap.child(Constant().KEY_LONGITUDE_KOS).value.toString()
                        val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        val snapSisa=snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString()
                        val snapFasilitas=snap.child(Constant().KEY_FASILITAS).value.toString()
                        val snapDeskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString()
                        val snapStatus=snap.child(Constant().KEY_STATUS_VERIFIKASI_KOS).value.toString()
                        namaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        jenisKos=snap.child(Constant().KEY_JENIS_KOS).value.toString()

                        if(jenisKos==Constant().KEY_WANITA  && snapStatus==Constant().KEY_TERVERIFIKASI)
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
                                fasilitas=snapFasilitas.toString(),
                                deskripsi=snapDeskripsi,
                                status=snapStatus

                            )
                            kosArrayList.add(kos)
                            adapter= HomeKosAdapter(kosArrayList,this@CampurKosFragment)
                            layoutManager=LinearLayoutManager(activity)
                            binding.rvkoscampur.layoutManager=layoutManager
                            binding.rvkoscampur.adapter=adapter
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }



    fun cariDataKosCampuran(cari:String)
    {

        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvkoscampur.adapter=null

                    snapshot.children.forEach { snap->
                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                        val snapBiaya=snap.child(Constant().KEY_BIAYA_KOS).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapGambarKos=snap.child(Constant().KEY_GAMBAR_KOS).value.toString() as ArrayList<String>
                        val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_KOS).value.toString()
                        val snapJenis=snap.child(Constant().KEY_JENIS_KOS).value.toString()
                        val snapJenisBayar=snap.child(Constant().KEY_JENIS_KELAMIN).value.toString()
                        val snapLattitude=snap.child(Constant().KEY_LATTITUDE_KOS).value.toString()
                        val snapLongitude=snap.child(Constant().KEY_LONGITUDE_KOS).value.toString()
                        val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        val snapSisa=snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString()
                        val snapFasilitas=snap.child(Constant().KEY_FASILITAS).value.toString()
                        val snapDeskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString()
                        val snapStatus=snap.child(Constant().KEY_STATUS_VERIFIKASI_KOS).value.toString()
                        namaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        jenisKos=snap.child(Constant().KEY_JENIS_KOS).value.toString()

                        if(jenisKos==Constant().KEY_PRIA && cari.contains(namaKos,true) && snapStatus==Constant().KEY_TERVERIFIKASI)
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
                                fasilitas=snapFasilitas.toString(),
                                deskripsi=snapDeskripsi,
                                status=snapStatus

                            )
                            kosArrayList.add(kos)
                            adapter= HomeKosAdapter(kosArrayList,this@CampurKosFragment)
                            layoutManager=LinearLayoutManager(activity)
                            binding.rvkoscampur.layoutManager=layoutManager
                            binding.rvkoscampur.adapter=adapter
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onClick(v: View, dataKos: Kos) {
        if(dataKos.sisa==0)
        {
            Toast.makeText(activity, "Mohon Maaf, Kos Sedang Penuh", Toast.LENGTH_SHORT).show()
        }

        else
        {
            val intent=Intent(activity, DetailSewaKosActivity::class.java).putExtra("dataKos", dataKos)
            startActivity(intent)
        }
    }


}