package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.Model.PermintaanVerifikasiKos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityTambahKosBinding
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TambahKosActivity : AppCompatActivity(),MapboxMap.OnMapClickListener {


    private var uriThumbnail: Uri?=null
    private var sliderUri:Uri?=null
    private var database=Firebase.database.reference
    private var firebaseStorage=FirebaseStorage.getInstance().reference
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var userId=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val slideImageArrayList=ArrayList<SlideModel>()
    private var gambarKosList=ArrayList<String>()
    private var marker:Marker?=null
    private var lattitudeKos=0.0
    private var longitudeKos=0.0
    private var calendar=Calendar.getInstance()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    private lateinit var binding:ActivityTambahKosBinding
    private lateinit var map:MapboxMap
    private lateinit var latLng: LatLng
    private lateinit var permintaanVerifikasiKos: PermintaanVerifikasiKos
    private lateinit var tanggalHariIni:String
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityTambahKosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@TambahKosActivity)

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(this@TambahKosActivity)

        Mapbox.getInstance(applicationContext,getString(R.string.access_token))

        setSpinner()

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        slideImageArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,  ScaleTypes.FIT))
        binding.sliderupload.setImageList(slideImageArrayList)

        binding.mapviewtambahkos.getMapAsync {mapbox->
            map=mapbox
            map.addOnMapClickListener(this)
        }


        binding.btnaddslideimage.setOnClickListener {
            ImagePicker.with(this@TambahKosActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent {intent->
                    kostImagePickerResult.launch(intent)
                }
        }

        binding.ivthumbnailkos.setOnClickListener {
            ImagePicker.with(this@TambahKosActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent {intent->
                    kosThubmnailPickerResult.launch(intent)
                }
        }



        binding.btntambah.setOnClickListener {
            if(!gagalValidasi())
            {

                val kosId=UUID.randomUUID().toString()
                val namaKos=binding.txtnamakos.text.trim().toString()
                val alamat=binding.txtalamatkos.text.trim().toString()
                val hargaHarian=binding.txthargaharian.text.trim().toString()
                val hargaBulanan=binding.txthargabulanan.text.trim().toString()
                val hargaTahunan=binding.txthargatahunan.text.trim().toString()
                val jenisBayar=binding.spnjenisbayar.selectedItem.toString()
                val gambarThumbnail="${Constant().KEY_GAMBAR_THUMBNAIL_KOS}/$kosId/${UUID.randomUUID()}"
                val gambarKosUrl="${Constant().KEY_GAMBAR_KOS}/$kosId/"
                val jenisKos=binding.spnjeniskos.selectedItem.toString()
                val jumlahKamar=binding.txtjumlahkamarkos.text.trim().toString()
                val fasilitas=binding.txtfasilitas.text.trim().toString()
                val deskripsi=binding.txtdeskripsi.text.trim().toString()
                val kecamatan=binding.txtkecamatankos.text.trim().toString()
                val kelurahan=binding.txtkelurahankos.text.trim().toString()

                for(i in slideImageArrayList.indices)
                {
                    gambarKosList.add("$gambarKosUrl$i${UUID.randomUUID()}")
                }

                val kos=Kos(
                    idKos=kosId,
                    namaKos=namaKos,
                    idPemilik=idPengguna,
                    emailPemilik=emailPengguna,
                    alamat = alamat,
                    kelurahan=kelurahan,
                    kecamatan=kecamatan,
                    hargaHarian=hargaHarian.toDouble(),
                    hargaBulanan=hargaBulanan.toDouble(),
                    hargaTahunan=hargaTahunan.toDouble(),
                    jenisBayar=jenisBayar,
                    gambarKos =gambarKosList,
                    thumbnailKos = gambarThumbnail,
                    jenis=jenisKos,
                    sisa = jumlahKamar.toInt(),
                    lattitude = lattitudeKos.toString(),
                    longitude =  longitudeKos.toString(),
                    fasilitas=fasilitas,
                    deskripsi=deskripsi,
                    status=Constant().KEY_PENGAJUAN_VERIFIKASI,
                    rating=0
                )

                permintaanVerifikasiKos=PermintaanVerifikasiKos(
                    email=emailPengguna,
                    idPemohon =idPengguna,
                    idPermintaan = UUID.randomUUID().toString(),
                    idKos=kosId,
                    username=preferenceManager.getString(Constant().KEY_USERNAME).toString(),
                    isi="$emailPengguna ingin melakukan verifikasi kos",
                    judul=Constant().KEY_PERMINTAAN_VERIFIKASI_KOS,
                    tanggal=tanggalHariIni
                )



                database.child(Constant().KEY_PERMINTAAN)
                    .child(kosId)
                    .setValue(permintaanVerifikasiKos)

                database.child(Constant().KEY_DAFTAR_KOS)
                    .child(kosId)
                    .setValue(kos)
                    .addOnSuccessListener {

                        firebaseStorage.child(gambarThumbnail).putFile(uriThumbnail!!)

                        for(i in slideImageArrayList.indices)
                        {
                            sliderUri=Uri.parse(slideImageArrayList[i].imageUrl)
                            firebaseStorage.child(gambarKosList[i]).putFile(sliderUri!!)
                        }


                        Toast.makeText(this@TambahKosActivity, "Sukses Menambah Kos Baru, Sekarang Tunggu Verifikasi Kos Anda Dari Admin", Toast.LENGTH_SHORT).show()
                        clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@TambahKosActivity, "Gagal Menambah Kos Baru", Toast.LENGTH_SHORT).show()
                    }

            }

            else
            {
                gagalValidasi()
            }

        }

        binding.btnaddslideimage.setOnClickListener {
            ImagePicker.with(this@TambahKosActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent->
                    kostImagePickerResult.launch(intent)
                }
        }


    }

    private var kosThubmnailPickerResult:ActivityResultLauncher<Intent> =  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
            result->

        if(result.resultCode == RESULT_OK)
        {
            uriThumbnail=result.data?.data
            binding.ivthumbnailkos.setImageURI(uriThumbnail!!)
        }

        else if(result.resultCode==ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        }

    }

    private var kostImagePickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
            result->

        if(result.resultCode== RESULT_OK)
        {
            sliderUri=result.data?.data

            if(slideImageArrayList[0].imagePath==R.drawable.placeholder_add_kos_slide)
            {
                slideImageArrayList.removeAt(0)
            }

            slideImageArrayList.add(SlideModel(sliderUri.toString(),scaleType = ScaleTypes.FIT))

            binding.sliderupload.setImageList(slideImageArrayList)

            binding.sliderupload.setItemClickListener(object:ItemClickListener{
                override fun onItemSelected(position: Int) {
                    dialogHapus(position)
                }
            })

        }

        else
        {
            Toast.makeText(this@TambahKosActivity, "Gagal Mengambil Gambar", Toast.LENGTH_SHORT).show()
        }


    }


    private fun setSpinner()
    {
        val arrayJenisKos= arrayOf(Constant().JENISKOS_SPINNER_DEFAULT,Constant().KEY_PRIA,Constant().KEY_WANITA,Constant().KEY_CAMPUR)
        val jenisKosAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayJenisKos)
        binding.spnjeniskos.adapter=jenisKosAdapter

        val arrayJenisBayar= arrayOf(Constant().JENISBAYAR_SPINNER_DEFAULT, Constant().KEY_TRANSFER, Constant().KEY_BAYARDITEMPAT)
        val jenisBayarAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item,arrayJenisBayar)
        binding.spnjenisbayar.adapter=jenisBayarAdapter
    }


    override fun onMapClick(point: LatLng) {
        val intent = Intent(this, MapActivity::class.java)
        getLatLong.launch(intent)
    }



    private var getLatLong = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        // Validity checks
        if (RESULT_OK != result.resultCode) {
            Toast.makeText(applicationContext, "Pengambilan Lokasi Dibatalkan", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        val intent = result.data
        if (intent == null)
        {
            Toast.makeText(applicationContext, "Activity hasn't returned an intent.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        if (!intent.hasExtra("lattitudeKos") && !intent.hasExtra("longitudeKos"))
        {
            Toast.makeText(applicationContext,  "Gagal Mengambil Lokasi Kos.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        // Valid result returned
        lattitudeKos=intent.getStringExtra("lattitudeKos")!!.toDouble()
        longitudeKos=intent.getStringExtra("longitudeKos")!!.toDouble()

        latLng=LatLng()
        latLng.latitude=lattitudeKos
        latLng.longitude=longitudeKos


        marker?.let {currentMarker->
            map.removeMarker(currentMarker)
        }

        marker=map.addMarker(MarkerOptions().position(latLng))


        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(15.0)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }



    private fun gagalValidasi():Boolean
    {
        var gagal=false

        val namaKos=binding.txtnamakos.text.trim()
        val alamatKos=binding.txtalamatkos.text.trim()
        val jenisKos=binding.spnjeniskos
        val jenisBayar=binding.spnjenisbayar
        val hargaKosHarian=binding.txthargaharian.text.trim()
        val hargaKosBulanan=binding.txthargaharian.text.trim()
        val hargaKosTahunan=binding.txthargaharian.text.trim()
        val fasilitasKos=binding.txtfasilitas.text.trim()
        val deskripsiKos=binding.txtdeskripsi.text.trim()

        if(namaKos.isNullOrEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Isi Nama Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(alamatKos.isNullOrEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Isi Alamat Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(jenisKos.selectedItemPosition==0)
        {
            Toast.makeText(applicationContext, "Silahkan Pilih Jenis Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(jenisBayar.selectedItemPosition==0)
        {
            Toast.makeText(applicationContext, "Silahkan Pilih Jenis Bayar", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(hargaKosHarian.isEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Masukkan Harga Kos Harian", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(hargaKosBulanan.isEmpty())
        {
            Toast.makeText(this@TambahKosActivity, "Silahkan Masukkan Harga Kos Bulanan", Toast.LENGTH_SHORT).show()
        }
        
        else if(hargaKosTahunan.isEmpty())
        {
            Toast.makeText(this@TambahKosActivity, "Silahkan Masukkan Harga Kos Tahunan", Toast.LENGTH_SHORT).show()
        }

        else if(fasilitasKos.isNullOrEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Masukkan Fasilitas Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(deskripsiKos.isNullOrEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Masukkan Deskripsi Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(uriThumbnail==null)
        {
            Toast.makeText(applicationContext, "Silahkan Ambil Foto Thumbnail Kos", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(slideImageArrayList[0].imagePath==R.drawable.placeholder_add_kos_slide)
        {
            Toast.makeText(applicationContext, "Silahkan Ambil Setidaknya Satu Foto Kos-Kosan", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        return gagal
    }

    private fun dialogHapus(posisi:Int)
    {
        val dialogBuilder=AlertDialog.Builder(this)
        dialogBuilder.setTitle("Hapus Gammbar?")
        dialogBuilder.setMessage("Hapus Gambar Ini Dari Daftar Gambar?")
        dialogBuilder.setPositiveButton("Hapus"){_, _->

            slideImageArrayList.removeAt(posisi)

            if(slideImageArrayList.isEmpty())
            {
                slideImageArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,ScaleTypes.FIT))
            }

            binding.sliderupload.setImageList(slideImageArrayList)

            binding.sliderupload.setItemClickListener(object : ItemClickListener{
                override fun onItemSelected(position: Int) {
                    dialogHapus(position)
                }

            })

        }

        dialogBuilder.setNegativeButton("Batalkan"){_, _ ->

        }

        dialogBuilder.show()
    }


    private fun clear()
    {
        binding.txtnamakos.text.clear()
        binding.txtalamatkos.text.clear()
        binding.txtjumlahkamarkos.text.clear()
        binding.spnjeniskos.setSelection(0)
        binding.spnjenisbayar.setSelection(0)
        binding.txthargaharian.text.clear()
        binding.txthargabulanan.text.clear()
        binding.txthargatahunan.text.clear()
        binding.txtfasilitas.text.clear()
        binding.txtdeskripsi.text.clear()

        uriThumbnail=null
        binding.ivthumbnailkos.setImageResource(R.drawable.placeholder_thumbnail_kos)

        sliderUri=null
        slideImageArrayList.clear()
        slideImageArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,ScaleTypes.FIT))
        binding.sliderupload.setImageList(slideImageArrayList)

        map.clear()


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