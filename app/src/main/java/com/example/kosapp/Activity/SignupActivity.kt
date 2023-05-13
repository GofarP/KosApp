package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.Model.Verifikasi
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivitySignupBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var preferenceManager: PreferenceManager

    private var uri: Uri?=null
    private lateinit var pengguna:Pengguna
    private lateinit var verifikasi:Verifikasi
    private  var storage = FirebaseStorage.getInstance()
    private var auth=FirebaseAuth.getInstance()
    val database=Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@SignupActivity)

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(this@SignupActivity)

        setSpinner()

        binding.btnsignup.setOnClickListener{

            if(!gagalValidasi())
            {
                auth.fetchSignInMethodsForEmail(binding.txtemail.text.toString())
                    .addOnCompleteListener {task->
                        val belumTerdaftar=task.result.signInMethods?.isEmpty()

                        if(belumTerdaftar == true)
                        {
                            signUp()
                        }

                        else
                        {
                            Toast.makeText(this@SignupActivity, "Email ini sudah digunakan", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            else
            {
                gagalValidasi()
            }

        }

        binding.ivprofile.setOnClickListener {
            ImagePicker.with(this@SignupActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent->
                    imageProfilePickResult.launch(intent)
                }
        }

        binding.lblsignin.setOnClickListener{
            startActivity(Intent(this@SignupActivity, SigninActivity::class.java))
        }
    }


    private fun gagalValidasi():Boolean
    {
        var gagal=false

        val username=binding.txtusername.text.trim()
        val email=binding.txtemail.text.trim()
        val noTelp=binding.txtnoTelp.text.trim()
        val password=binding.txtpassword.text.trim()
        val konfirmasiPassword=binding.txtpassword.text.trim()

        if(username.isNullOrEmpty())
        {
            Toast.makeText(this@SignupActivity, "Silahkan Isi Username Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(binding.spinnerjk.selectedItemPosition==0)
        {
            Toast.makeText(this@SignupActivity, "Silahkan Pilih Jenis Kelamin Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(email.isNullOrEmpty())
        {
            Toast.makeText(this@SignupActivity, "Silahkan Isi Email Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(noTelp.isNullOrEmpty())
        {
            Toast.makeText(this@SignupActivity, "Silahkan Isi No Telpon Anda", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(password.isNullOrEmpty())
        {
            Toast.makeText(this@SignupActivity, "Silahkan Isi Password Anda Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(konfirmasiPassword.isNullOrEmpty())
        {
            Toast.makeText(this@SignupActivity, "Silahkan Isi Konfirmasi Password", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        else if(password!=konfirmasiPassword)
        {
            Toast.makeText(this@SignupActivity, "Password Dan Konfirmasi Passowrd Tidak Cocok", Toast.LENGTH_SHORT).show()
            gagal=true
        }

        return  gagal
    }

    private fun signUp()
    {
        val username=binding.txtusername.text.toString()
        val email=binding.txtemail.text.toString()
        val noTelp=binding.txtnoTelp.text.toString()
        val jenisKelamin=binding.spinnerjk.selectedItem.toString()
        val password=binding.txtpassword.text.toString()
        val nik=binding.txtnik.text.toString()
        val kecamatan=binding.txtkecamatan.text.toString()
        val kelurahan=binding.txtkelurahan.text.toString()


        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->

            val idPengguna=task.result.user?.uid
            val emailPengguna=task.result.user?.email
            val imgName="${Constant().KEY_PROFILE_PICTURE}/${idPengguna}${UUID.randomUUID()}"

            pengguna=Pengguna(
                id=idPengguna.toString(),
                username = username,
                email = email,
                noTelp = noTelp,
                jenisKelamin=jenisKelamin,
                foto=imgName,
                nik=nik,
                kelurahan=kelurahan,
                kecamatan=kecamatan,
                role=Constant().KEY_ROLE_USER
            )

            verifikasi= Verifikasi(
                idVerifikasi=UUID.randomUUID().toString(),
                idPengguna = idPengguna.toString(),
                email =emailPengguna.toString(),
                username=username,
                foto = "",
                status = Constant().KEY_BELUM_VERIFIKASI
            )

            database.child(Constant().KEY_USER)
                .child(idPengguna.toString())
                .setValue(pengguna)
                .addOnSuccessListener {
                    if(uri==null)
                    {
                        uri = Uri.parse(
                            "android.resource://" + (com.example.kosapp.R::class.java.getPackage()
                                ?.name ) + "/" + R.drawable.kindpng_2855863
                        )
                    }

                    database.child(Constant().KEY_VERIFIKASI)
                        .child(idPengguna.toString())
                        .setValue(verifikasi)

                    storage.reference.child(imgName).putFile(uri!!)

                    preferenceManager.putString(Constant().KEY_ROLE,Constant().KEY_ROLE_USER)

                    Toast.makeText(this@SignupActivity, "Sukses Membuat Akun", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, SigninActivity::class.java))
                }
                .addOnFailureListener {exception->
                    Toast.makeText(this@SignupActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
        }

    }



    private fun setSpinner()
    {
        val arrayJenisKelamin=arrayOf("Pilih Jenis Kelamin",Constant().KEY_PRIA,Constant().KEY_WANITA)
        val arrayAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayJenisKelamin)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerjk.adapter=arrayAdapter
    }


    private var imageProfilePickResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    {result->
        if(result.resultCode== RESULT_OK)
        {
            uri=result.data?.data
            binding.ivprofile.setImageURI(uri)
        }

        else
        {
            Toast.makeText(this@SignupActivity, "Gagal Mengambil Gambar Profil", Toast.LENGTH_SHORT).show()
        }

    }


}