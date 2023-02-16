package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenuChatAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.MenuChat
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

class MenuChatActivity : AppCompatActivity(),MenuChatAdapter.ItemOnClickMenuChat {

    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var  database= Firebase.database.reference
    private var storage=Firebase.storage.reference
    private var arrayListMenuChat=ArrayList<MenuChat>()

    private lateinit var binding:MenuChatActivityBinding
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var adapter:MenuChatAdapter
    private lateinit var menuChat: MenuChat
    private lateinit var idMenuChat: String
    private lateinit var emailPengirim:String
    private lateinit var emailPenerima:String
    private lateinit var fotoPenerima:String
    private lateinit  var fotoPengirim:String
    private lateinit  var usernamePenerima:String
    private lateinit  var usernamePengirim:String
    private lateinit  var pesanTerakhir:String





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= MenuChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@MenuChatActivity)

        getAkunPengguna()

        getMenuChatData()

    }


    private fun getMenuChatData()
    {
        database.child(Constant().KEY_MENU_CHAT)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListMenuChat.clear()
                    snapshot.children.forEach { snap->
                        idMenuChat=snap.child(Constant().KEY_ID_MENU_CHAT).value.toString()
                        emailPenerima=snap.child(Constant().KEY_EMAIL_PENERIMA).value.toString()
                        emailPengirim=snap.child(Constant().KEY_EMAIL_PENGIRIM).value.toString()
                        fotoPenerima=snap.child(Constant().KEY_FOTO_PENERIMA).value.toString()
                        fotoPengirim=snap.child(Constant().KEY_FOTO_PENGIRIM).value.toString()
                        usernamePenerima=snap.child(Constant().KEY_USER_PENERIMA).value.toString()
                        usernamePengirim=snap.child(Constant().KEY_USER_PENGIRIM).value.toString()
                        pesanTerakhir=snap.child(Constant().KEY_PESAN_TERAKHIR).value.toString()

                        if(emailPenerima==emailPengguna || emailPengirim==emailPengguna)
                        {
                            menuChat= MenuChat(
                                idMenuChat,
                                emailPenerima,
                                emailPengirim,
                                fotoPenerima,
                                fotoPengirim,
                                usernamePenerima,
                                usernamePengirim,
                                pesanTerakhir
                            )

                            arrayListMenuChat.add(menuChat)
                        }
                    }

                    adapter= MenuChatAdapter(arrayListMenuChat,emailPengguna,this@MenuChatActivity)
                    layoutManager=LinearLayoutManager(this@MenuChatActivity)
                    binding.rvmenuchat.layoutManager=layoutManager
                    binding.rvmenuchat.adapter=adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
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

    override fun onClick(v: View, menuChat: MenuChat) {
        val intent=Intent(this@MenuChatActivity, ChatActiviity::class.java)
            .putExtra(Constant().KEY_DATA,menuChat.emailPenerima)
        startActivity(intent)
    }
}