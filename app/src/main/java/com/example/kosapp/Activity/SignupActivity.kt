package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.kosapp.Model.Akun
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivitySignupBinding
import com.github.dhaval2404.imagepicker.ImagePicker

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private var uri: Uri?=null
    private lateinit var akun:Akun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var window=this@SignupActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(this@SignupActivity, R.color.main_color)

        setSpinner()

        binding.btnsignup.setOnClickListener{
            if(!gagalValidasi())
            {
                signUp()
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
        val noTelp=binding.txtnoTelp.toString()
        val jenisKelamin=binding.spinnerjk.selectedItem.toString()
        val foto="foto"
        val password=binding.txtpassword.text.toString()

        akun=Akun(
            id = "random",
            username = username,
            email = email,
            noTelp = noTelp,
            jenisKelamin=jenisKelamin,
            foto=foto,
            password=password
        )

        Toast.makeText(this@SignupActivity, "Mendaftar", Toast.LENGTH_SHORT).show()

    }



    private fun setSpinner()
    {
        val arrayJenisKelamin=arrayOf("Pilih Jenis Kelamin","Laki-Laki","Wanita")
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