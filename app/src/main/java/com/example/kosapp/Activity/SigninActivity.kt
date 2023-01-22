package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kosapp.Helper.Helper
import com.example.kosapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private  var auth= FirebaseAuth.getInstance()
    private var user=FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@SigninActivity)

        if(user!=null)
        {
            startActivity(Intent(this@SigninActivity, MainActivity::class.java))
            finish()
        }


        binding.btnsignin.setOnClickListener{
            if(validation())
            {
                Helper().showToast(this@SigninActivity,"Silahkan Isi Kolom Yang Masih Kosong")
            }

            else
            {
                signIn()
            }

        }

        binding.lblsignup.setOnClickListener {
            startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
        }


    }


    private fun validation():Boolean
    {
        var kosong=false
        val email=binding.txtemail.text.trim()
        val password=binding.txtpassword.text.trim()

        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            kosong=true
        }

        return kosong
    }



    private fun signIn()
    {
        val email=binding.txtemail.text.trim().toString()
        val password=binding.txtpassword.text.trim().toString()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            result->
            
            if(result.isSuccessful)
            {
                startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                finish()
            }

            else
            {
                Toast.makeText(this@SigninActivity, "Gagal Login", Toast.LENGTH_SHORT).show()
            }

        }

    }


}