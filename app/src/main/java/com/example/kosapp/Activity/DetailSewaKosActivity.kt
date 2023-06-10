package com.example.kosapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Callback.SetImageListCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.BuktiTransfer
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityDetailSewaKosBinding
import com.example.kosapp.databinding.LayoutWaktuSewaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetailSewaKosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSewaKosBinding
    private lateinit var bindingWaktuSewa:LayoutWaktuSewaBinding
    private var slideHashMap=HashMap<String,SlideModel>()
    private var slideArrayList=ArrayList<SlideModel>()
    private var storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private var emailPengguna=Firebase.auth.currentUser?.email.toString()
    private lateinit var dataKosIntent:Intent
    private lateinit var kos:Kos
    private lateinit var permintaan: Permintaan
    private var permintaanDitemukan=false
    private var kosSudahDisewa=false
    private var calendar=Calendar.getInstance()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var total:Double=0.0
    private lateinit var jumlahHari:String
    private lateinit var tanggalHariIni:String
    private lateinit var buktiTransfer: BuktiTransfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailSewaKosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tanggalHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        Helper().setStatusBarColor(this@DetailSewaKosActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra(Constant().KEY_ID_KOS)!!

        if(emailPengguna==kos.idPemilik)
        {
            binding.btnpesan.visibility=View.INVISIBLE
            binding.btnchatpemilik.visibility=View.INVISIBLE
        }


        checkSewaKos()


        setDataKos()
        

        setGambarKos(object :SetImageListCallback{

            override fun setImageList(listGambarKos: ArrayList<SlideModel>) {
                binding.includeLayoutDetail.sliderDetailKos.setImageList(listGambarKos)
            }

        })



        binding.btnaddcomment.setOnClickListener {
            val intent=Intent(this@DetailSewaKosActivity, CommentActivity::class.java)
            intent.putExtra(Constant().KEY_ID_KOS,kos.idKos)
            intent.putExtra(Constant().KEY_EMAIL_PEMILIK,kos.idPemilik)
            startActivity(intent)
        }

        binding.btnchatpemilik.setOnClickListener {
            val intent=Intent(this@DetailSewaKosActivity, ChatActiviity::class.java)
                .putExtra(Constant().KEY_EMAIL_PENGIRIM,emailPengguna)
                .putExtra(Constant().KEY_EMAIL_PENERIMA,kos.idPemilik)
            startActivity(intent)
        }

        binding.btnpesan.setOnClickListener {

            if(permintaanDitemukan)
            {
                Toast.makeText(this@DetailSewaKosActivity, "Permintaan Anda Sedang Di Proses", Toast.LENGTH_SHORT).show()
            }

            else if(kosSudahDisewa)
            {
                Toast.makeText(this@DetailSewaKosActivity, "Kos Ini Sudah Anda Sewa", Toast.LENGTH_SHORT).show()
            }

            else
            {

                val dialogSewa=layoutInflater.inflate(R.layout.layout_waktu_sewa, null)
                val customDialog=AlertDialog
                    .Builder(this)
                    .setView(dialogSewa)
                    .show()

                bindingWaktuSewa= LayoutWaktuSewaBinding.inflate(layoutInflater)
                customDialog.setContentView(bindingWaktuSewa.root)

                val arrayWaktu=arrayOf(Constant().KEY_HARI, Constant().KEY_BULAN)

                val waktuProfileAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayWaktu)
                waktuProfileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                bindingWaktuSewa.spnsatuanwaktu.adapter=waktuProfileAdapter


                val textWatcher=object:TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        jumlahHari=bindingWaktuSewa.txtjmlhari.text.toString()


                        if(jumlahHari.isEmpty())
                        {
                            total=0.0
                        }

                        else
                        {
                            total=jumlahHari.toDouble() * kos.biaya
                        }

                        bindingWaktuSewa.lbltotalharga.text=NumberFormat.getCurrencyInstance().format(total)

                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }

                }


                bindingWaktuSewa.btnwaktusewa.setOnClickListener {
                    if(total!=0.0)
                    {
                        sewaKos()
                        customDialog.dismiss()

                    }

                    else
                    {
                        Toast.makeText(this@DetailSewaKosActivity, "Silahkan Isi Jangka Waktu Penyewan kos", Toast.LENGTH_SHORT).show()
                    }
                }


                bindingWaktuSewa.txtjmlhari.addTextChangedListener (textWatcher )

            }

        }

    }

    private fun sewaKos()
    {
        permintaan=Permintaan(
            idPermintaan=UUID.randomUUID().toString(),
            idKos=kos.idKos,
            idPenyewa = idPengguna,
            idPemilik=kos.idPemilik,
            emailPenyewa=emailPengguna,
            emailPemilik =kos.emailPemilik,
            namaKos=kos.namaKos,
            judul = Constant().KEY_PERMINTAAN_SEWA,
            isi ="Mengajukan Permintaan Untuk Menyewa Kos ${kos.namaKos}",
            tanggal = tanggalHariIni,
        )

        database.child(Constant().KEY_PERMINTAAN)
            .push()
            .ref.setValue(permintaan)
            .addOnSuccessListener {

                binding.btnpesan.isEnabled=false
                binding.btnpesan.setBackgroundResource(R.drawable.button_background_disabled)
                binding.btnpesan.text="Permintaan Diproses"

                database.child(Constant().KEY_DAFTAR_KOS)
                    .child(kos.idKos)
                    .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                    .setValue(ServerValue.increment(-1))

                Toast.makeText(this@DetailSewaKosActivity, "Sukses Mengajukan Permintaan Untuk Menyewa Kos", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this@DetailSewaKosActivity, "Gagal Mengajukan Permintaan Untuk Menyewa Kos", Toast.LENGTH_SHORT).show()
            }
    }



    private fun checkSewaKos()
    {

        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {snap->
                        val emailDari=snap.child(Constant().KEY_DARI).value.toString()
                        val idKos=snap.child(Constant().KEY_ID_KOS).value.toString()

                        if(emailDari==emailPengguna && idKos==kos.idKos)
                        {
                            binding.btnpesan.isEnabled=false
                            binding.btnpesan.setBackgroundResource(R.drawable.button_background_disabled)
                            binding.btnpesan.text="Permintaan Diproses"
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                   Log.d("Database Error",error.message)
                }

            })


        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->

                        val emailPenyewa=snap.child(Constant().KEY_EMAIL).value.toString()
                        val idKos=snap.child(Constant().KEY_ID_KOS).value.toString()

                        if(emailPenyewa==emailPengguna && idKos==kos.idKos)
                        {
                            binding.btnpesan.isEnabled=false
                            binding.btnpesan.setBackgroundResource(R.drawable.button_background_disabled)
                            binding.btnpesan.text="Kos Sudah Disewa"
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }



        private fun setGambarKos(setImageListCallback: SetImageListCallback)
        {
            kos.gambarKos.indices.forEachIndexed { _, i ->
                storage.child(kos.gambarKos[i])
                    .downloadUrl
                    .addOnSuccessListener {uri->

                        slideArrayList.add(SlideModel(uri.toString(), ScaleTypes.FIT))
                        setImageListCallback.setImageList(slideArrayList)

                    }
                    .addOnFailureListener {
                        Toast.makeText(this@DetailSewaKosActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }



    private fun setDataKos()
    {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2

        binding.includeLayoutDetail.lblnamakos.text= kos.namaKos
        binding.includeLayoutDetail.lblfasilitas.text= kos.fasilitas
        binding.includeLayoutDetail.lblhargakos.text=  format.format(kos.biaya)
        binding.includeLayoutDetail.lbljenispembayaran.text=kos.jenisBayar
        binding.includeLayoutDetail.lbljeniskos.text=kos.jenis
        binding.includeLayoutDetail.lbldeskripsikos.text=kos.deskripsi

    }

}