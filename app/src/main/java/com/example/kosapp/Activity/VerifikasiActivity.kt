package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.Model.Verifikasi
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityVerifikasiBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class VerifikasiActivity : AppCompatActivity() {

    private var database=FirebaseDatabase.getInstance().reference
    private val idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val emailPengguna=FirebaseAuth.getInstance().currentUser?.email

    private val storage=FirebaseStorage.getInstance().reference
    private var uriVerifikasi: Uri?=null
    private var calendar=Calendar.getInstance()


    private lateinit var fotoVerifikasi:String
    private lateinit var verifikasi:Verifikasi
    private lateinit var permintaanVerifikasi:PermintaanVerifikasi
    private lateinit var binding:ActivityVerifikasiBinding
    private lateinit var tanggalHariIni:String
    private lateinit var username:String
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var idPermintaan:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@VerifikasiActivity)

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(this@VerifikasiActivity)

        checkVerifikasi()

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        username= preferenceManager.getString(Constant().KEY_USERNAME).toString()

        binding.btnuploadverifikasi.setOnClickListener {
            if(uriVerifikasi==null)
            {
                Toast.makeText(this@VerifikasiActivity, "Silahkan Pilih Foto KTP Yang Mau di Upload", Toast.LENGTH_SHORT).show()
            }

            else
            {
                uploadVerifikasi()
            }
        }

        binding.ivVerifikasi.setOnClickListener {
            ImagePicker.with(this@VerifikasiActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent {intent->
                    verifikasiResult.launch(intent)
                }
        }


    }

    private fun checkVerifikasi()
    {

        database.child(Constant().KEY_VERIFIKASI)
            .child(idPengguna.toString())
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val snapFoto=snapshot.child(Constant().KEY_FOTO).value.toString()
                    val snapUsername=snapshot.child(Constant().KEY_USERNAME).value.toString()

                    if(snapshot.exists())
                    {

                        val snapVerifikasi=snapshot.child(Constant().KEY_STATUS_VERIFIKASI_AKUN).value.toString()

                        binding.lblstatusverifikasi.text=snapVerifikasi

                        if(snapVerifikasi==Constant().KEY_TERVERIFIKASI || snapVerifikasi==Constant().KEY_PENGAJUAN_VERIFIKASI)
                        {
                            binding.lblstatusverifikasi.setTextColor(ContextCompat.getColor(this@VerifikasiActivity, R.color.green_ok))

                            storage.child(snapFoto).downloadUrl.addOnSuccessListener { url->
                                Glide.with(this@VerifikasiActivity)
                                    .load(url)
                                    .into(binding.ivVerifikasi)

                            }.addOnFailureListener {
                                binding.ivVerifikasi.setImageResource(R.drawable.placeholder_verifikasi)
                            }

                        }

                        else if(snapVerifikasi==Constant().KEY_BELUM_VERIFIKASI)
                        {
                            binding.lblstatusverifikasi.setTextColor(ContextCompat.getColor(this@VerifikasiActivity, R.color.red_cancel))
                        }

                    }

                    else
                    {
                        binding.ivVerifikasi.setImageResource(R.drawable.placeholder_verifikasi)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }


    private fun uploadVerifikasi()
    {
        fotoVerifikasi="${Constant().KEY_VERIFIKASI}/$idPengguna/$idPengguna"


        idPermintaan=UUID.randomUUID().toString()

        verifikasi= Verifikasi(
            idVerifikasi = UUID.randomUUID().toString(),
            idPengguna =idPengguna,
            email = emailPengguna.toString(),
            username=username,
            foto =fotoVerifikasi,
            status = Constant().KEY_PENGAJUAN_VERIFIKASI
        )


        permintaanVerifikasi= PermintaanVerifikasi(
            idPermintaan = idPermintaan,
            idPemohon=idPengguna,
            email = emailPengguna.toString(),
            judul = Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN,
            isi="$emailPengguna ingin melakukan verifikasi akun",
            username=username,
            tanggal = tanggalHariIni
        )

        database.child(Constant().KEY_VERIFIKASI)
            .child(idPengguna)
            .setValue(verifikasi)
            .addOnSuccessListener {
                storage.child(fotoVerifikasi).putFile(uriVerifikasi!!)

                database.child(Constant().KEY_USER)
                    .child(idPengguna)
                    .child(Constant().KEY_VERIFIKASI)
                    .setValue(Constant().KEY_PENGAJUAN_VERIFIKASI)

            }.addOnFailureListener {
                Toast.makeText(this@VerifikasiActivity, "Gagal Verifikasi Akun", Toast.LENGTH_SHORT).show()
            }


        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(idPermintaan)
            .setValue(permintaanVerifikasi)
            .addOnSuccessListener {
                Toast.makeText(this@VerifikasiActivity, "Berhasil Mengajukan Permintaan Verifikasi AKun", Toast.LENGTH_SHORT).show()
                binding.lblstatusverifikasi.text="Pengajuan"


                preferenceManager.putString(Constant().KEY_STATUS_VERIFIKASI_AKUN,Constant().KEY_PENGAJUAN_VERIFIKASI)
            }
            .addOnFailureListener {
                Toast.makeText(this@VerifikasiActivity, "Gagal Mengajukan Permintaan Verifikasi Akun", Toast.LENGTH_SHORT).show()
            }
    }

    private var verifikasiResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode== RESULT_OK)
        {
            uriVerifikasi=result.data?.data
            binding.ivVerifikasi.setImageURI(uriVerifikasi!!)
        }
    }



}