package com.example.kosapp.Fragment

import android.content.Intent
import android.location.Location
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
import com.example.kosapp.LocationManager.LocationManager
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentSemuaKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import java.text.DecimalFormat


class SemuaKosFragment : Fragment(), ItemOnClick {

    private lateinit var binding:FragmentSemuaKosBinding
    private  var kosArrayList=ArrayList<Kos>()
    private var cariKosArrayList=ArrayList<Kos>()
    private var homeKosAdapter:HomeKosAdapter?=null
    private var database=Firebase.database.reference
    private lateinit var kos:Kos
    private lateinit var  layoutManager: RecyclerView.LayoutManager
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var namaKos:String
    private lateinit var locationEngine: LocationEngine
    lateinit var lokasiSekarang: Location
    private lateinit var lokasiSekarangLatLng: Point
    private lateinit var lokasiKosLatLng: Point
    private var jarak=0.0
    private lateinit var  locationManager:LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSemuaKosBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        locationManager=LocationManager()
        locationManager.ambilLokasiSekarang(requireActivity())

        lokasiSekarangLatLng=Point.fromLngLat(locationManager.ambilLokasiSekarang(requireActivity()).longitude,
            locationManager.ambilLokasiSekarang(requireActivity()).latitude)

        getSemuaDataKos()

    }


        fun getSemuaDataKos()
        {
            database.child(Constant().KEY_DAFTAR_KOS)
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        kosArrayList.clear()
                        binding.rvkossemua.adapter=null

                        snapshot.children.forEach { snap->

                            val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                            val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                            val snapKelurahan=snap.child(Constant().KEY_KELURAHAN).value.toString()
                            val snapKecamatan=snap.child(Constant().KEY_KECAMATAN).value.toString()
                            val snapBiaya=snap.child(Constant().KEY_BIAYA_KOS).value.toString()
                            val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                            val snapGambarKos=snap.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>
                            val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()
                            val snapJenis=snap.child(Constant().KEY_JENIS_KOS).value.toString()
                            val snapJenisBayar=snap.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString()
                            val snapLattitude=snap.child(Constant().KEY_LATTITUDE_KOS).value.toString()
                            val snapLongitude=snap.child(Constant().KEY_LONGITUDE_KOS).value.toString()
                            val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                            val snapSisa=snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString()
                            val snapFasilitas=snap.child(Constant().KEY_FASILITAS).value.toString()
                            val snapDeskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString()
                            val snapStatus=snap.child(Constant().KEY_STATUS_VERIFIKASI_KOS).value.toString()
                            val snapRating=snap.child(Constant().KEY_RATING).value.toString().toInt()
                            lokasiKosLatLng=Point.fromLngLat(snapLongitude.toDouble(), snapLattitude.toDouble())
                            jarak= TurfMeasurement.distance(lokasiSekarangLatLng,lokasiKosLatLng, TurfConstants.UNIT_KILOMETERS)


                            if(snapStatus==Constant().KEY_TERVERIFIKASI)
                            {
                                kos=Kos(
                                    idKos=snapIdKos,
                                    alamat = snapAlamat,
                                    kelurahan=snapKelurahan,
                                    kecamatan=snapKecamatan,
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
                                    status=snapStatus,
                                    rating=snapRating,
                                    jarak=DecimalFormat("#.##").format(jarak).toDouble()
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
                        Log.d("dberror",error.message)
                    }

                })
        }


        fun cariSemuaDataKos( cari:String)
        {
            cariKosArrayList.clear()
            kosArrayList.forEach {result->
                val cari=cari.trim().replace(" ","")
                val namaKos=result.nama.trim().replace(" ","")
                val alamatKos=result.alamat.trim().replace(" ","")

                if(namaKos.contains(cari, true) || alamatKos.contains(cari, true))
                {
                    kos=Kos(
                        idKos=result.idKos,
                        nama=result.nama,
                        emailPemilik = result.emailPemilik,
                        jenis = result.jenis,
                        alamat=result.alamat,
                        biaya = result.biaya,
                        deskripsi = result.deskripsi,
                        fasilitas = result.fasilitas,
                        gambarKos = result.gambarKos,
                        jenisBayar = result.jenisBayar,
                        kecamatan = result.kecamatan,
                        kelurahan = result.kelurahan,
                        lattitude = result.lattitude,
                        longitude = result.longitude,
                        sisa=result.sisa,
                        status = result.status,
                        thumbnailKos = result.thumbnailKos,
                        rating = result.rating,
                        jarak=result.jarak
                    )
                    cariKosArrayList.add(kos)
                }
            }

            homeKosAdapter= HomeKosAdapter(cariKosArrayList,this@SemuaKosFragment)
            layoutManager=LinearLayoutManager(activity)
            binding.rvkossemua.layoutManager=layoutManager
            binding.rvkossemua.adapter=homeKosAdapter
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
            val intent=Intent(activity, DetailSewaKosActivity::class.java).putExtra(Constant().KEY_ID_KOS, dataKos)
            startActivity(intent)
        }

    }
}