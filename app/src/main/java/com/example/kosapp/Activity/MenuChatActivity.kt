package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityChatBinding
import com.example.kosapp.databinding.MenuChatActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MenuChatActivity : AppCompatActivity() {

    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var  database= Firebase.database.reference
    private var storage=Firebase.storage.reference

    private lateinit var binding:MenuChatActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= MenuChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@MenuChatActivity)

        getAkunPengguna()

    }


    private fun getAkunPengguna()
    {
        database.child(Constant().KEY_USER)
            .child(idPengguna)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                        val username=snapshot.child(Constant().KEY_USERNAME).value.toString()
                        val fotoProfil=snapshot.child(Constant().KEY_FOTO).value.toString()

                        binding.lblnamapengguna.text=username

                        storage.child(fotoProfil).downloadUrl.addOnSuccessListener {uri->
                            Glide.with(this@MenuChatActivity)
                                .load(uri)
                                .into(binding.ivprofile)

                        }.addOnFailureListener {
                            Log.d("profile image",it.message.toString())
                        }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }
}