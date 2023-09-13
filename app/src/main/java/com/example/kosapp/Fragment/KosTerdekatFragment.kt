package com.example.kosapp.Fragment

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailSewaKosActivity
import com.example.kosapp.Activity.SigninActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentKosTerdekatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import java.text.DecimalFormat


class KosTerdekatFragment : Fragment(), HomeKosAdapter.ItemOnClick, LocationListener {

    private lateinit var binding:FragmentKosTerdekatBinding
    private  var kosArrayList=ArrayList<Kos>()
    private  var cariKosArrayList=ArrayList<Kos>()
    private var adapter: HomeKosAdapter?=null
    private var database= Firebase.database.reference
    private var jarak=0.0
    private var emailPemilik=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var rentang=false
    private var auth=FirebaseAuth.getInstance().currentUser

    private lateinit var kos: Kos
    private lateinit var  layoutManager: RecyclerView.LayoutManager
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var namaKos:String
    private lateinit var locationEngine: LocationEngine
    private lateinit var lokasiSekarang:Location
    private lateinit var lokasiSekarangLatLng:Point
    private lateinit var lokasiKosLatLng:Point
    private lateinit var locationManager: LocationManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentKosTerdekatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationManager= requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(requireActivity())

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lokasiSekarangLatLng=Point.fromLngLat(location!!.longitude, location.latitude)

        getSemuaKosTerdekat()

        val arrayFilterHarga=arrayOf(Constant().KEY_SEMUA,Constant().KEY_TERMURAH, Constant().KEY_TERMAHAL)
        val filterHargaAdapter= ArrayAdapter(requireContext(), R.layout.simple_spinner_item, arrayFilterHarga)
        filterHargaAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spnfilterharga.adapter=filterHargaAdapter

        binding.spnfilterharga.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected=p0?.getItemAtPosition(p2).toString()
                //filterHarga(selected)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }


    fun getSemuaKosTerdekat()
    {

        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvkosterdekat.adapter=null

                    snapshot.children.forEach { snap->

                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                        val snapKelurahan=snap.child(Constant().KEY_KELURAHAN).value.toString()
                        val snapKecamatan=snap.child(Constant().KEY_KECAMATAN).value.toString()
                        val snapHargaHarian=snap.child(Constant().KEY_HARGA_KOS_HARIAN).value.toString()
                        val snapHargaBulanan=snap.child(Constant().KEY_HARGA_KOS_BULANAN).value.toString()
                        val snapHargaTahunan=snap.child(Constant().KEY_HARGA_KOS_TAHUNAN).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapIdPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString()
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
                        namaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        lokasiKosLatLng=Point.fromLngLat(snapLongitude.toDouble(), snapLattitude.toDouble())
                        jarak= TurfMeasurement.distance(lokasiSekarangLatLng,lokasiKosLatLng, TurfConstants.UNIT_KILOMETERS)

                        if(snapStatus== Constant().KEY_TERVERIFIKASI && jarak < 4.0)
                        {

                            kos=Kos(
                                idKos=snapIdKos,
                                alamat = snapAlamat,
                                kelurahan=snapKelurahan,
                                kecamatan=snapKecamatan,
                                hargaHarian = snapHargaHarian.toDouble(),
                                hargaBulanan = snapHargaBulanan.toDouble(),
                                hargaTahunan = snapHargaTahunan.toDouble(),
                                idPemilik=snapIdPemilik,
                                emailPemilik=snapEmailPemilik,
                                gambarKos = snapGambarKos,
                                thumbnailKos = snapThumbnailKos,
                                jenis=snapJenis,
                                jenisBayar = snapJenisBayar,
                                lattitude = snapLattitude,
                                longitude = snapLongitude,
                                namaKos = snapNamaKos,
                                sisa = snapSisa.toInt(),
                                fasilitas=snapFasilitas.toString(),
                                deskripsi=snapDeskripsi,
                                status=snapStatus,
                                rating=snapRating,
                                jarak = DecimalFormat("#.##").format(jarak).replace(",",".").toDouble()
                            )
                            kosArrayList.add(kos)
                            adapter= HomeKosAdapter(kosArrayList,this@KosTerdekatFragment)
                            layoutManager= LinearLayoutManager(activity)
                            binding.rvkosterdekat.layoutManager=layoutManager
                            binding.rvkosterdekat.adapter=adapter
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun cariKosTerdekat( cari:String)
    {
        cariKosArrayList.clear()
        kosArrayList.forEach {result->
            val cari=cari.trim().replace(" ","")
            val namaKos=result.namaKos.trim().replace(" ","")
            val alamatKos=result.alamat.trim().replace(" ","")

            if(namaKos.contains(cari, true) || alamatKos.contains(cari, true))
            {
                kos=Kos(
                    idKos=result.idKos,
                    namaKos=result.namaKos,
                    idPemilik = result.idPemilik,
                    emailPemilik=result.emailPemilik,
                    jenis = result.jenis,
                    alamat=result.alamat,
                    hargaHarian = result.hargaHarian,
                    hargaBulanan = result.hargaBulanan,
                    hargaTahunan = result.hargaTahunan,
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

        adapter= HomeKosAdapter(cariKosArrayList,this@KosTerdekatFragment)
        layoutManager=LinearLayoutManager(activity)
        binding.rvkosterdekat.layoutManager=layoutManager
        binding.rvkosterdekat.adapter=adapter

    }

    fun filterHarga(filter:String)
    {
        cariKosArrayList.clear()
        kosArrayList.forEach {result->

            when(filter)
            {
                Constant().KEY_TERMURAH->{
                    rentang=result.hargaBulanan > 0
                }

                Constant().KEY_SEDANG->{
                    rentang=result.hargaBulanan > 400000.00 && result.hargaBulanan <=700000.00
                }

                Constant().KEY_TERMAHAL->{
                    rentang=result.hargaBulanan > 700000.00 && result.hargaBulanan <=1000000.00
                }

                Constant().KEY_SANGAT_MAHAL->{
                    rentang=result.hargaBulanan > 1000000.00
                }
            }

            if(rentang)
            {
                kos=Kos(
                    idKos=result.idKos,
                    namaKos=result.namaKos,
                    idPemilik = result.idPemilik,
                    emailPemilik=result.emailPemilik,
                    jenis = result.jenis,
                    alamat=result.alamat,
                    hargaHarian = result.hargaHarian,
                    hargaBulanan = result.hargaBulanan,
                    hargaTahunan = result.hargaTahunan,
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

        adapter= HomeKosAdapter(cariKosArrayList,this@KosTerdekatFragment)
        layoutManager=LinearLayoutManager(activity)
        binding.rvkosterdekat.layoutManager=layoutManager
        binding.rvkosterdekat.adapter=adapter

    }



    override fun onClick(v: View, dataKos: Kos) {
        val jenisKelaminUser=preferenceManager.getString(Constant().KEY_JENIS_KELAMIN)


        if(dataKos.sisa==0)
        {
            Toast.makeText(activity, "Mohon Maaf, Kos Sedang Penuh", Toast.LENGTH_SHORT).show()
        }

        else if(dataKos.jenis != jenisKelaminUser && dataKos.jenis!="Campur" && dataKos.idPemilik!= emailPemilik && auth!=null)
        {
            Toast.makeText(activity, "Jenis Kelamin Anda Tidak Cocok Untuk Kos Ini $jenisKelaminUser", Toast.LENGTH_SHORT).show()
        }

        else
        {
            val intent= Intent(activity, DetailSewaKosActivity::class.java).putExtra(Constant().KEY_DATA_KOS, dataKos)
            startActivity(intent)
        }
    }

    override fun onLocationChanged(p0: Location) {
        println("test")
    }

}