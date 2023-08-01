package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.KosImageCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.GambarKos
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityEditKosBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

class EditKosActivity : AppCompatActivity(), OnMapClickListener {

    private lateinit var binding:ActivityEditKosBinding

    private lateinit var latLng: LatLng
    private lateinit var map: MapboxMap
    private var marker: Marker?=null

    private var storage=FirebaseStorage.getInstance().reference
    private var database= Firebase.database.reference
    private var userEmail=FirebaseAuth.getInstance().currentUser?.email

    private var slideImageArrayList=ArrayList<SlideModel>()

    private var gambarKosDihapusList=ArrayList<String>()
    private var gambarKosBaruList=ArrayList<String>()
    private var gambarKosLamaList=ArrayList<String>()
    private var gambarKosArrayList=ArrayList<GambarKos>()

    private  var hargaHarian=0.0
    private  var hargaBulanan=0.0
    private  var hargaTahunan=0.0


    private lateinit var kos:Kos
    private lateinit var gambarKos:GambarKos

    private var uriThumbail: Uri? = null
    private var sliderUri:Uri?=null
    private var sliderUriList=ArrayList<String>()

    private lateinit var dataKosIntent:Intent

    private lateinit var lattitude:String
    private lateinit var longitude:String
    private lateinit var alamat:String

    private lateinit var deskripsi:String
    private lateinit var fasilitas:String
    private lateinit var idKos:String
    private lateinit var kelurahan:String
    private lateinit var kecamatan:String

    private var jenis:Int?=null
    private var jenisBayar:Int?=null
    private lateinit var nama:String
    private lateinit var sisa:String
    private lateinit var gambarThumbnail:String

    private var thumbnailDiganti=false

    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditKosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        binding.mapviewtambahkos.onCreate(savedInstanceState)

        Helper().setStatusBarColor(this@EditKosActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra(Constant().KEY_DATA_KOS)!!

        setDataEditKos()


        editKosCallback(object :KosImageCallback{

            override fun setImageList(arrayListImageList: ArrayList<GambarKos>) {
                arrayListImageList.forEach {urlGambarKos->
                    slideImageArrayList.add(SlideModel(urlGambarKos.url, scaleType = ScaleTypes.FIT))
                }

                binding.sliderupload.setImageList(slideImageArrayList)
                binding.sliderupload.setItemClickListener(object: ItemClickListener{
                    override fun onItemSelected(position: Int) {
                        dialogHapus(position)
                    }

                })

            }


            override fun setImageThumbnail(uri: String) {
                Glide.with(this@EditKosActivity)
                    .load(uri)
                    .into(binding.ivthumbnailkos)
            }

        })




        binding.btnedit.setOnClickListener {
//            checkDeletedImage()
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

        binding.btnaddslideimage.setOnClickListener {
            ImagePicker.with(this@EditKosActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent->
                    gambarKosPickerResult.launch(intent)
                }
        }
    }


    private fun setDataEditKos()
    {

        //set value on spinner
        val arrayJenisKos= arrayOf(
            Constant().JENISKOS_SPINNER_DEFAULT,
            Constant().KEY_PRIA,
            Constant().KEY_WANITA,
            Constant().KEY_CAMPUR)
        val jenisKosAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayJenisKos)
        binding.spnjeniskos.adapter=jenisKosAdapter

        val arrayJenisBayar= arrayOf(Constant().JENISBAYAR_SPINNER_DEFAULT, Constant().KEY_TRANSFER, Constant().KEY_BAYARDITEMPAT)
        val jenisBayarAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item,arrayJenisBayar)
        binding.spnjenisbayar.adapter=jenisBayarAdapter

        gambarKosLamaList=kos.gambarKos

        val jenisKosPosition=jenisKosAdapter.getPosition(kos.jenis)
        val jenisBayarPosition=jenisBayarAdapter.getPosition(kos.jenisBayar)

        binding.txtnamakos.setText(kos.namaKos)
        binding.txtalamatkos.setText(kos.alamat)
        binding.txtjumlahkamarkos.setText(kos.sisa.toString())
        binding.spnjeniskos.setSelection(jenisKosPosition)
        binding.spnjenisbayar.setSelection(jenisBayarPosition)
        binding.txthargaharian.setText(kos.hargaHarian.toString())
        binding.txthargabulanan.setText(kos.hargaBulanan.toString())
        binding.txthargatahunan.setText(kos.hargaTahunan.toString())
        binding.txtfasilitas.setText(kos.fasilitas)
        binding.txtdeskripsi.setText(kos.deskripsi)
        binding.txtkelurahankos.setText(kos.kelurahan)
        binding.txtkecamatankos.setText(kos.kecamatan)
        lattitude=kos.lattitude
        longitude=kos.longitude
        gambarThumbnail=kos.thumbnailKos
        idKos=kos.idKos


        binding.mapviewtambahkos.getMapAsync{mapboxMap->
            map=mapboxMap
            latLng=LatLng()
            latLng.latitude= lattitude.toDouble()
            latLng.longitude= longitude.toDouble()
            map.addMarker(MarkerOptions().position(latLng))

            map.addOnMapClickListener(this)
        }
    }


    fun gagalValidasi():Boolean
    {
        var gagal=false

        alamat=binding.txtalamatkos.text.trim().toString()
        hargaHarian=binding.txthargaharian.text.trim().toString().toDouble()
        hargaBulanan= binding.txthargabulanan.text.trim().toString().toDouble()
        hargaTahunan=binding.txthargatahunan.text.trim().toString().toDouble()
        deskripsi=binding.txtdeskripsi.text.trim().toString()
        deskripsi=binding.txtdeskripsi.text.trim().toString()
        fasilitas=binding.txtfasilitas.text.trim().toString()
        idKos=kos.idKos
        jenis=binding.spnjeniskos.selectedItemPosition
        jenisBayar=binding.spnjenisbayar.selectedItemPosition
        nama=binding.txtnamakos.text.trim().toString()
        sisa=binding.txtjumlahkamarkos.text.trim().toString()

        if(alamat.isEmpty())
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Alamat", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(hargaHarian!=0.0)
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Harga Kos Harian", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(hargaBulanan!=0.0)
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Harga Kos Bulanan", Toast.LENGTH_SHORT).show()
        }

        else if(hargaTahunan!=0.0)
        {
            Toast.makeText(this@EditKosActivity, "Silahkan Isi Harga Kos Tahunan", Toast.LENGTH_SHORT).show()
        }

        else if(deskripsi.isEmpty())
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
        hargaHarian=binding.txthargaharian.text.trim().toString().toDouble()
        hargaBulanan=binding.txthargabulanan.text.trim().toString().toDouble()
        hargaTahunan=binding.txthargatahunan.text.toString().toDouble()
        deskripsi=binding.txtdeskripsi.text.trim().toString()
        fasilitas=binding.txtfasilitas.text.trim().toString()
        idKos=kos.idKos
        val jenis=binding.spnjeniskos.selectedItem.toString()
        val jenisBayar=binding.spnjenisbayar.selectedItem.toString()
        nama=binding.txtnamakos.text.trim().toString()
        sisa=binding.txtjumlahkamarkos.text.trim().toString()
        kelurahan=binding.txtkelurahankos.text.trim().toString()
        kecamatan=binding.txtkecamatankos.text.trim().toString()

        kos=Kos(
            idKos=idKos,
            namaKos=nama,
            alamat = alamat,
            hargaHarian =hargaHarian,
            hargaBulanan =hargaBulanan,
            hargaTahunan =hargaTahunan,
            idPemilik=idPengguna,
            emailPemilik = emailPengguna,
            jenisBayar=jenisBayar,
            gambarKos =kos.gambarKos,
            thumbnailKos = gambarThumbnail,
            jenis=jenis,
            sisa = sisa.toInt(),
            lattitude = lattitude,
            longitude =  longitude,
            fasilitas=fasilitas,
            deskripsi=deskripsi,
            kecamatan=kecamatan,
            kelurahan=kelurahan,
            status=Constant().KEY_PENGAJUAN_VERIFIKASI,
        )

        database.child(Constant().KEY_DAFTAR_KOS)
            .child(idKos)
            .setValue(kos)
            .addOnSuccessListener {

                if(thumbnailDiganti)
                {
                    storage.child(gambarThumbnail).delete()
                    storage.child(gambarThumbnail).putFile(uriThumbail!!)
                }

                if(gambarKosDihapusList.size>0)
                {
                    database.child(Constant().KEY_DAFTAR_KOS)
                        .child(kos.idKos)
                        .child(Constant().KEY_GAMBAR_KOS)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEachIndexed {index, snap->
                                    if(snap.value.toString() in gambarKosDihapusList)
                                    {
                                        storage.child(snap.value.toString()).delete()
                                        snap.ref.removeValue()
                                        gambarKosLamaList.remove(snap.value.toString())
                                    }
                                }


                                database.child(Constant().KEY_DAFTAR_KOS)
                                    .child(kos.idKos)
                                    .child(Constant().KEY_GAMBAR_KOS)
                                    .setValue(gambarKosLamaList)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("DB Error",error.message)
                            }

                        })


                }

                if(gambarKosBaruList.size>0)
                {
                    gambarKosBaruList.forEachIndexed {index,_->
                        storage.child(gambarKosBaruList[index])
                            .putFile(sliderUriList[index].toUri())
                    }

                    gambarKosBaruList.addAll(gambarKosLamaList)

                    database.child(Constant().KEY_DAFTAR_KOS)
                        .child(kos.idKos)
                        .child(Constant().KEY_GAMBAR_KOS)
                        .setValue(gambarKosBaruList)

                }
                Toast.makeText(this@EditKosActivity, "Sukses Mengubah Data Kos", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@EditKosActivity, KosSayaActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this@EditKosActivity, "Gagal Mengubah Data Kos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dialogHapus(position: Int)
    {
        Toast.makeText(this@EditKosActivity, gambarKosArrayList[position].name, Toast.LENGTH_SHORT).show()
        val dialogBuilder= AlertDialog.Builder(this)
        dialogBuilder.setTitle("Hapus Gambar?")
        dialogBuilder.setMessage("Hapus Gambar Ini Dari Daftar Gambar?")
        dialogBuilder.setPositiveButton("Hapus"){_,_->

            if(slideImageArrayList.size==1)
            {
                Toast.makeText(applicationContext, "Sisakan Setidaknya Satu Gambar Kos", Toast.LENGTH_SHORT).show()
            }

            else
            {
                gambarKosDihapusList.add(gambarKosArrayList[position].name)
                slideImageArrayList.removeAt(position)
                gambarKosArrayList.removeAt(position)
                binding.sliderupload.setImageList(slideImageArrayList)
            }


            binding.sliderupload.setItemClickListener(object:ItemClickListener{
                override fun onItemSelected(position: Int) {
                    dialogHapus(position)
                }

            })

        }

        dialogBuilder.setNegativeButton("Batalkan"){_,_->

        }

        dialogBuilder.show()
    }

    private var kosThumbailPickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode== RESULT_OK)
        {
            gambarThumbnail="${Constant().KEY_GAMBAR_THUMBNAIL_KOS}/${kos.idKos}/${UUID.randomUUID()}"
            thumbnailDiganti=true
            uriThumbail=result.data?.data
            binding.ivthumbnailkos.setImageURI(uriThumbail)
        }
    }

    private var gambarKosPickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {result->

        if(result.resultCode== RESULT_OK)
        {
            val gambarKosUrl="${Constant().KEY_GAMBAR_KOS}/$idKos/${UUID.randomUUID()}"

            sliderUri=result.data?.data

            sliderUriList.add(sliderUri.toString())

            gambarKosBaruList.add(gambarKosUrl)

        }

    }


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

        if(!intent?.hasExtra(Constant().KEY_LATTITUDE_KOS)!! && !intent.hasExtra(Constant().KEY_LONGITUDE_KOS))
        {
            Toast.makeText(this@EditKosActivity, "Gagal Mengambil Lokasi Kos", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        //Valid Result Returned
        val latLng=LatLng()
        latLng.latitude=intent.getStringExtra(Constant().KEY_LATTITUDE_KOS)!!.toDouble()
        latLng.longitude=intent.getStringExtra(Constant().KEY_LONGITUDE_KOS)!!.toDouble()

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


    private fun editKosCallback(kosImageCallback: KosImageCallback)
    {
        for (item in kos.gambarKos)
        {
            storage.child(item)
                .downloadUrl
                .addOnSuccessListener {url->

                    gambarKos=GambarKos(url.toString(),item)
                    gambarKosArrayList.add(gambarKos)

                    if(gambarKosArrayList.size==kos.gambarKos.size)
                    {
                        kosImageCallback.setImageList(gambarKosArrayList)
                    }


                }
        }

        storage.child(kos.thumbnailKos)
            .downloadUrl
            .addOnSuccessListener { uri->
                kosImageCallback.setImageThumbnail(uri.toString())
            }
    }


    override fun onStart() {
        super.onStart()
        binding.mapviewtambahkos.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapviewtambahkos.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapviewtambahkos.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapviewtambahkos.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapviewtambahkos.onDestroy()
    }



}