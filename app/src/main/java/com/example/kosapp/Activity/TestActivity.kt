package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Adapter.RecyclerviewAdapter.NegaraAdapter
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityTestBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TestActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTestBinding
    private lateinit var adapter:NegaraAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private val database=Firebase.database.reference
    private val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var sliderArrayList=ArrayList<SlideModel>()
    private var storage=Firebase.storage.reference
    private lateinit var testIntent: Intent
    private lateinit var kos:Kos
    private lateinit var sliderUri:Uri
    private var dihapus=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sliderArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,  ScaleTypes.FIT))
        binding.sliderupload.setImageList(sliderArrayList)




        binding.btntest.setOnClickListener {
            ImagePicker.with(this@TestActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent {intent->
                    kostImagePickerResult.launch(intent)
                }
        }
    }

    private var kostImagePickerResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->

        if (result.resultCode == RESULT_OK) {
            sliderUri = result.data?.data!!


            if (sliderArrayList[0].imagePath == R.drawable.placeholder_add_kos_slide) {
                sliderArrayList.removeAt(0)
            }


            sliderArrayList.add(SlideModel(sliderUri.toString(), scaleType = ScaleTypes.FIT))

            binding.sliderupload.setImageList(sliderArrayList)

            binding.sliderupload.setItemClickListener(object: ItemClickListener {
                override fun onItemSelected(position: Int) {
                    dialogHapus(position)
                }

            })



        } else {
            Toast.makeText(this@TestActivity, "Gagal Mengambil Gambar", Toast.LENGTH_SHORT).show()
        }

    }

    private fun dialogHapus(posisi:Int)
    {
        val dialogBuilder= AlertDialog.Builder(this)
        dialogBuilder.setTitle("Hapus Gammbar?")
        dialogBuilder.setMessage("Hapus Gambar Ini Dari Daftar Gambar?")
        dialogBuilder.setPositiveButton("Hapus"){_, _->

            sliderArrayList.removeAt(posisi)

            if(sliderArrayList.isEmpty())
            {
                sliderArrayList.add(SlideModel(R.drawable.placeholder_add_kos_slide,ScaleTypes.FIT))
            }

            binding.sliderupload.setImageList(sliderArrayList)

            binding.sliderupload.setItemClickListener(object:ItemClickListener{
                override fun onItemSelected(position: Int) {
                    dialogHapus(position)
                }

            })

        }

        dialogBuilder.setNegativeButton("Batalkan"){_, _ ->

        }

        dialogBuilder.show()
    }






}