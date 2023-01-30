package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityEditKosBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapClickListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class EditKosActivity : AppCompatActivity(), OnMapClickListener {

    private lateinit var binding:ActivityEditKosBinding

    private lateinit var latLng: LatLng
    private lateinit var map: MapboxMap
    private var marker: Marker?=null

    private var storage=FirebaseStorage.getInstance().reference
    private var database= Firebase.database.reference
    private var userEmail=FirebaseAuth.getInstance().currentUser?.email

    private var slideArrayList=ArrayList<SlideModel>()

    private lateinit var kos:Kos

    private var uriThumbail: Uri? = null
    private var sliderUri:Uri?=null

    private lateinit var dataKosIntent:Intent


    private var lattitude=""
    private var longitude=""
    private lateinit var alamat:String
    private lateinit var biaya:String
    private lateinit var deskripsi:String
    private lateinit var fasilitas:String
    private lateinit var kosId:String
    private var jenis:Int?=null
    private var jenisBayar:Int?=null
    private lateinit var nama:String
    private lateinit var sisa:String
    private lateinit var gambarThumbnail:String

    private var thumbnailBaru=false
    private var gambarKosDiganti=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditKosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        binding.mapviewtambahkos.onCreate(savedInstanceState)

        Helper().setStatusBarColor(this@EditKosActivity)



        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!

        setDataEditKos()

        binding.btnedit.setOnClickListener {
            if(!gagalValidasi())
            {
                editDataKos()
            }
            else
            {
                gagalValidasi()
            }

        }

        binding.ivthumbnailkos.setOnClickListener {
            ImagePicker.with(this@EditKosActivity)
                . crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent->
                    kosThumbailPickerResult.launch(intent)
                }
        }

    }


    fun setDataEditKos()
    {

        //set value on spinner
        val arrayJenisKos= arrayOf("Pilih Jenis Kos","Pria","Wanita","Campur")
        val jenisKosAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayJenisKos)
        val arrayJenisBayar= arrayOf("Pilih Jenis Bayar", "Bayar Di Tempat", "Bayar Transfer")
        val jenisBayarAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item,arrayJenisBayar)
        binding.spnjeniskos.adapter=jenisKosAdapter
        binding.spnjenisbayar.adapter=jenisBayarAdapter


        val jenisKosPosition=jenisKosAdapter.getPosition(kos.jenis)
        val jenisBayarPosition=jenisBayarAdapter.getPosition(kos.jenisBayar)

        binding.txtnamakos.setText(kos.nama)
        binding.txtalamatkos.setText(kos.alamat)
        binding.txtjumlahkamarkos.setText(kos.sisa.toString())
        binding.spnjeniskos.setSelection(jenisKosPosition)
        binding.spnjenisbayar.setSelection(jenisBayarPosition)
        binding.txtharga.setText(kos.biaya.toString())
        binding.txtfasilitas.setText(kos.fasilitas)
        binding.txtdeskripsi.setText(kos.deskripsi)
        lattitude=kos.lattitude
        longitude=kos.longitude
        gambarThumbnail=kos.gambarThumbnail


        binding.mapviewtambahkos.getMapAsync{mapboxMap->
            map=mapboxMap
            latLng=LatLng()
            latLng.latitude= kos.lattitude.toDouble()
            latLng.longitude= kos.longitude.toDouble()
            map.addMarker(MarkerOptions().position(latLng))

            map.addOnMapClickListener(this)
        }


        storage.child(kos.gambarThumbnail)
            .downloadUrl
            .addOnSuccessListener { uri->
                Glide.with(this@EditKosActivity)
                    .load(uri)
                    .into(binding.ivthumbnailkos)
            }


        kos.gambarKos.indices.forEachIndexed { _, i ->
           storage.child(kos.gambarKos[i])
               .downloadUrl
               .addOnSuccessListener { uri->
                    slideArrayList.add(SlideModel(uri.toString(),ScaleTypes.FIT))

                    if(i ==  kos.gambarKos.size -1)
                    {
                        binding.sliderupload.setImageList(slideArrayList)
                    }
               }
        }

    }


    fun gagalValidasi():Boolean
    {
        var gagal=false

        alamat=binding.txtalamatkos.text.trim().toString()
        biaya=binding.txtalamatkos.text.trim().toString()
        deskripsi=binding.txtdeskripsi.text.trim().toString()
        fasilitas=binding.txtfasilitas.text.trim().toString()
        kosId=kos.id
        jenis=binding.spnjeniskos.selectedItemPosition
        jenisBayar=binding.spnjenisbayar.selectedItemPosition
        nama=binding.txtnamakos.text.trim().toString()
        sisa=binding.txtjumlahkamarkos.text.trim().toString()

        if(alamat.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Alamat", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(biaya.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Biaya Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(deskripsi.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Deskripsi Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(fasilitas.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Fasilitas Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(jenis==0)
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Pilih Jenis Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(jenisBayar==0)
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Pilih Jenis Bayar", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(nama.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Nama Kos", Toast.LENGTH_SHORT).show()
            gagal=false
        }

        else if(sisa.isNullOrEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi jumlah Kamar Kos", Toast.LENGTH_SHORT).show()
            gagal=false
        }

        return gagal
    }

    fun editDataKos()
    {
        alamat=binding.txtalamatkos.text.trim().toString()
        biaya=binding.txtharga.text.trim().toString()
        deskripsi=binding.txtdeskripsi.text.trim().toString()
        fasilitas=binding.txtfasilitas.text.trim().toString()
        kosId=kos.id
        val jenis=binding.spnjeniskos.selectedItem.toString()
        val jenisBayar=binding.spnjenisbayar.selectedItem.toString()
        nama=binding.txtnamakos.text.trim().toString()
        sisa=binding.txtjumlahkamarkos.text.trim().toString()


        kos=Kos(
            id=kosId,
            nama=nama,
            alamat = alamat,
            biaya =biaya.toDouble(),
            jenisBayar=jenisBayar,
            gambarKos =kos.gambarKos,
            gambarThumbnail = gambarThumbnail,
            jenis=jenis,
            sisa = sisa.toInt(),
            lattitude = lattitude,
            longitude =  longitude,
            fasilitas=fasilitas,
            deskripsi=deskripsi,
        )

        database.child("daftarKos")
            .child(userEmail.toString().replace(".",","))
            .child(kosId)
            .setValue(kos)
            .addOnSuccessListener {

                if(thumbnailBaru)
                {
                    storage.child(kos.gambarThumbnail).delete()
                    storage.child(gambarThumbnail).putFile(uriThumbail!!)
                }

                if(gambarKosDiganti)
                {

                }

                Toast.makeText(this@EditKosActivity, "Sukses Mengubah Data Kos", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@EditKosActivity, KosSayaActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this@EditKosActivity, "Gagal Mengubah Data Kos", Toast.LENGTH_SHORT).show()
            }


    }

    private var kosThumbailPickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode== RESULT_OK)
        {
            gambarThumbnail="thumbnailKos/${kos.id}/${UUID.randomUUID()}"
            thumbnailBaru=true
            uriThumbail=result.data?.data
            binding.ivthumbnailkos.setImageURI(uriThumbail)
        }
    }

//    private var gambarKosPickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult())
//    {result->
//
//        if(result.resultCode== RESULT_OK)
//        {
//            gambarKosDiganti=true
//            sliderUri=result.data?.data
//
//        }
//
//    }


    private var getLatLong=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {   result->

        if(RESULT_OK != result.resultCode)
        {
            Toast.makeText(this@EditKosActivity, "Pengambilan Lokasi Dibatalkan", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        val intent = result.data

        if(intent == null)
        {
            Toast.makeText(this@EditKosActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
        }

        if(!intent?.hasExtra("lattitudeKos")!! && !intent.hasExtra("longitudeKos"))
        {
            Toast.makeText(this@EditKosActivity, "Gagal Mengambil Lokasi Kos", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        //Valid Result Returned
        val latLng=LatLng()
        latLng.latitude=intent.getStringExtra("lattitudeKos")!!.toDouble()
        latLng.longitude=intent.getStringExtra("longitudeKos")!!.toDouble()

        lattitude=latLng.latitude.toString()
        longitude=latLng.longitude.toString()

        marker?.let {currentMarker->
            map.removeMarker(currentMarker)
        }

        marker=map.addMarker(MarkerOptions().position(latLng))

        val cameraPosition=CameraPosition.Builder()
            .target(latLng)
            .zoom(15.0)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

    override fun onMapClick(point: LatLng) {
        val intent = Intent(this@EditKosActivity, MapActivity::class.java)
        getLatLong.launch(intent)
    }


}