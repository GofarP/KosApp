package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.KosImageCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.GambarKos
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.databinding.ActivityDetailVerifikasiKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class DetailVerifikasiKosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVerifikasiKosBinding
    private lateinit var idKos:String
    private lateinit var gambarKos: GambarKos

    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference
    private var gambarKosArrayList=ArrayList<GambarKos>()
    private var slideImageArrayList=ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailVerifikasiKosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Helper().setStatusBarColor(this@DetailVerifikasiKosActivity)

        idKos=intent.getStringExtra(Constant().KEY_ID_KOS)!!


        getDataVerifikasiKos(object :KosImageCallback{
            override fun setImageList(arrayListImageList: ArrayList<GambarKos>) {

                arrayListImageList.forEach {urlGambarKos->
                    slideImageArrayList.add(SlideModel(urlGambarKos.url, scaleType = ScaleTypes.FIT))
                }

                binding.sliderdetailkos.setImageList(slideImageArrayList)
                binding.sliderdetailkos.setItemClickListener(object: ItemClickListener {
                    override fun onItemSelected(position: Int) {

                    }

                })

            }

            override fun setImageThumbnail(uri: String) {
                Glide.with(this@DetailVerifikasiKosActivity)
                    .load(uri)
                    .into(binding.ivdetailthumbnail)
            }

        })



    }


    private fun getDataVerifikasiKos(kosImageCallback: KosImageCallback)
    {
        database.child(Constant().KEY_DAFTAR_KOS)
            .child(idKos)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val snapNamaKos=snapshot.child(Constant().KEY_NAMA_KOS).value.toString()
                    val snapAlamatKos=snapshot.child(Constant().KEY_ALAMAT_KOS).value.toString()
                    val snapKelurahanKos=snapshot.child(Constant().KEY_KELURAHAN).value.toString()
                    val snapKecamatanKos=snapshot.child(Constant().KEY_KECAMATAN).value.toString()
                    val snapEmailPemilik=snapshot.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                    val snapHargaKos=snapshot.child(Constant().KEY_BIAYA_KOS).value.toString()
                    val snapJenisKos=snapshot.child(Constant().KEY_JENIS_KOS).value.toString()
                    val snapDeskripsiKos=snapshot.child(Constant().KEY_DESKRIPSI).value.toString()
                    val snapFasilitasKos=snapshot.child(Constant().KEY_FASILITAS).value.toString()
                    val snapThumbnailKos=snapshot.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()
                    val snapJenisPembayaran=snapshot.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString()
                    val snapGambarKos=snapshot.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>

                    binding.lblnamakos.text=snapNamaKos
                    binding.lblalamatkos.text=snapAlamatKos
                    binding.lblkecamatankos.text=snapKecamatanKos
                    binding.lblkelurahankos.text=snapKelurahanKos
                    binding.lblemailpemilik.text=snapEmailPemilik
                    binding.lblhargakos.text=snapHargaKos
                    binding.lbljeniskos.text=snapJenisKos
                    binding.lbldeskripsikos.text=snapDeskripsiKos
                    binding.lblfasilitaskos.text=snapFasilitasKos
                    binding.lbljenispembayaran.text=snapJenisPembayaran

                    storage.child(snapThumbnailKos).downloadUrl
                        .addOnSuccessListener { uri->
                            kosImageCallback.setImageThumbnail(uri.toString())
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@DetailVerifikasiKosActivity, "Gagal Mengambil Thumbnail Kos", Toast.LENGTH_SHORT).show()
                        }

                    for(item in snapGambarKos.indices)
                    {
                        storage.child(snapGambarKos[item])
                            .downloadUrl.addOnSuccessListener {uri->

                                gambarKos= GambarKos(uri.toString(), item.toString())

                                gambarKosArrayList.add(gambarKos)

                                if(gambarKosArrayList.size==snapGambarKos.size)
                                {
                                    kosImageCallback.setImageList(gambarKosArrayList)
                                }

                            }
                            .addOnFailureListener {
                                Toast.makeText(this@DetailVerifikasiKosActivity, "Gagal Mengambil Gambar Kos", Toast.LENGTH_SHORT).show()
                            }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }




}