package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    var database= Firebase.database.reference
    private lateinit var binding:ActivityProfileBinding
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@ProfileActivity)

        binding.btngantiusername.setOnClickListener {
            val username=binding.txtgantiusernam.text.trim().toString()
            if(username.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Username Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }

            else
            {
                updateUsername(username)
            }
        }

        binding.btngantipassword.setOnClickListener {
            val password=binding.txtgantipassword.text.trim().toString()

            if(password.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }

            else
            {
                updatePassword(password)
            }
        }
    }


    private fun updateUsername(username:String)
    {
        database.child(Constant().USER)
            .child(idPengguna)
            .child(Constant().KEY_USERNAME)
            .setValue(username)
            .addOnSuccessListener {
                Toast.makeText(this@ProfileActivity, "Sukses Mengubah Username", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@ProfileActivity, "Gagal Mengubah Username", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePassword(password:String)
    {

    }


    private fun dialogKonfirmasi()
    {

    }

}