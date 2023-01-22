package com.example.kosapp.Activity

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityTambahKosBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap


class TambahKosActivity : AppCompatActivity(), PermissionsListener,MapboxMap.OnMapClickListener {

    private lateinit var binding:ActivityTambahKosBinding

    private  var  uri: Uri?=null
    private var sliderUri:Uri?=null

    private val slideImageArrayList=ArrayList<SlideModel>()

    private lateinit var map:MapboxMap
    private lateinit var latLng: LatLng
    private var marker:Marker?=null
    private var lattitudeKos=0.0
    private var longitudeKos=0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityTambahKosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@TambahKosActivity)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))

        setSpinner()

        slideImageArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,  ScaleTypes.FIT))
        binding.sliderupload.setImageList(slideImageArrayList)




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

        binding.mapviewtambahkos.getMapAsync {mapbox->
            map=mapbox
            map.addOnMapClickListener(this)
        }

        binding.btntambah.setOnClickListener {
            if(!gagalValidasi())
            {
                tambahKost()
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
            uri=result.data?.data
            binding.ivthumbnailkos.setImageURI(uri!!)
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
        val arrayJenisKos= arrayOf("Pilih Jenis Kos","Laki-Laki","Wanita","Campur")
        val jenisKosAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayJenisKos)
        binding.spnjeniskos.adapter=jenisKosAdapter

        val arrayJenisBayar= arrayOf("Pilih Jenis Bayar", "Bayar Di Tempat", "Bayar Transfer")
        val jenisBayarAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item,arrayJenisBayar)
        binding.spnjenisbayar.adapter=jenisBayarAdapter
    }



    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(applicationContext, "Need To Enbale Permission", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {

    }



    override fun onMapClick(point: LatLng) {
        val intent = Intent(this, MapActivity::class.java)
        getLatLong.launch(intent)
    }



    private var getLatLong = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        // Validity checks
        if (RESULT_OK != result.resultCode) {
            Toast.makeText(applicationContext, "Pengambilan Lokasi Dibatalkan", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        val intent = result.data
        if (intent == null) {
            Toast.makeText(applicationContext, "Activity hasn't returned an intent.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        if (!intent.hasExtra("lattitudeKos") && !intent.hasExtra("longitudeKos")) {

            Toast.makeText(applicationContext,  "Activity hasn't returned extra data.", Toast.LENGTH_SHORT).show()
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
            .target(latLng) // Sets the center of the map to Mountain View
            .zoom(15.0) // Sets the zoom
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    private fun tambahKost()
    {
        //
    }

    private fun gagalValidasi():Boolean
    {
        var gagal=false

        val namaKos=binding.txtnamakos.text.trim()
        val alamatKos=binding.txtalamatkos.text.trim()
        val jenisKos=binding.spnjeniskos
        val jenisBayar=binding.spnjenisbayar
        val hargaKos=binding.txtharga.text.trim()
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

        else if(hargaKos.isNullOrEmpty())
        {
            Toast.makeText(applicationContext, "Silahkan Masukkan Harga Kos", Toast.LENGTH_SHORT).show()
            gagal=true
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

        else if(uri==null)
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

        }

        dialogBuilder.setNegativeButton("Batalkan"){_, _ ->

        }

        dialogBuilder.show()
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