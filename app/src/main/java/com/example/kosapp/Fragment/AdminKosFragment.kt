package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Activity.DetailVerifikasiKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
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
    private lateinit var kriteria:String
    private lateinit var keyword:String

    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference
    private var kosArrayList=ArrayList<Kos>()
    private var cariKosArrayList=ArrayList<Kos>()


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
        setSpinner()


        binding.spnfilterkos.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                kriteria=binding.spnfilterkos.selectedItem.toString()

                if(kriteria=="Semua Kos")
                {
                    getDataKos()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        binding.textinputlayout.setEndIconOnClickListener {

            keyword=binding.txtsearchkos.text.toString()
            kriteria=binding.spnfilterkos.selectedItem.toString()
            cariKosArrayList.clear()

            when(kriteria)
            {
                "Kelurahan"->{

                    for(i in kosArrayList.indices)
                    {
                        if(kosArrayList[i].kelurahan.lowercase().equals(keyword,ignoreCase=true))
                        {
                            cariKosArrayList.add(kosArrayList[i])
                        }
                    }

                }

                "Kecamatan"->{
                    for(i in kosArrayList.indices)
                    {
                        if(kosArrayList[i].kecamatan.lowercase().equals(keyword, ignoreCase=true))
                        {
                            cariKosArrayList.add(kosArrayList[i])
                        }
                    }
                }
            }

            adapter= HomeKosAdapter(cariKosArrayList,this@FragmentAdminKos)
            linearLayoutManager=LinearLayoutManager(activity)
            binding.rvaadminkos.layoutManager=linearLayoutManager
            binding.rvaadminkos.adapter=adapter

        }

    }


    private fun setSpinner()
    {
        val arrayFilterKos= arrayOf("Semua Kos","Kelurahan","Kecamatan")
        val jenisKosAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayFilterKos)
        binding.spnfilterkos.adapter=jenisKosAdapter
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
                        val snapKelurahan=snap.child(Constant().KEY_KELURAHAN).value.toString()
                        val snapKecamatan=snap.child(Constant().KEY_KECAMATAN).value.toString()
                        val snapBiayaHarian=snap.child(Constant().KEY_HARGA_KOS_HARIAN).value.toString()
                        val snapBiayaBulanan=snap.child(Constant().KEY_HARGA_KOS_BULANAN).value.toString()
                        val snapBiayaTahunan=snap.child(Constant().KEY_HARGA_KOS_TAHUNAN).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapIdPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString()
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
                                kelurahan=snapKelurahan,
                                kecamatan=snapKecamatan,
                                hargaHarian = snapBiayaHarian.toDouble(),
                                hargaBulanan = snapBiayaHarian.toDouble(),
                                hargaTahunan = snapBiayaHarian.toDouble(),
                                idPemilik=snapIdPemilik,
                                gambarKos = snapGambarKos,
                                thumbnailKos = snapThumbnailKos,
                                jenis=snapJenis,
                                jenisBayar = snapJenisBayar,
                                lattitude = snapLattitude,
                                longitude = snapLongitude,
                                namaKos = snapNamaKos,
                                sisa = snapSisa.toInt(),
                                fasilitas=snapFasilitas,
                                deskripsi=snapDeskripsi,
                                status=snapStatus,
                                emailPemilik=snapEmailPemilik
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
        val intent=Intent(requireActivity(), DetailVerifikasiKosActivity::class.java).putExtra(Constant().KEY_ID_KOS, dataKos.idKos)
        startActivity(intent)
    }


}