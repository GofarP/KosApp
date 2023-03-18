package com.example.kosapp.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.ChatAdapter
import com.example.kosapp.Callback.ChatCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Chat
import com.example.kosapp.Model.MenuChat
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.databinding.ActivityChatBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActiviity : AppCompatActivity() {

    private val database= Firebase.database.reference
    private val emailSaatIni=FirebaseAuth.getInstance().currentUser?.email.toString()
    private val storage=FirebaseStorage.getInstance().reference
    private var menuChatDataDitemukan=false
    private var arrayListPemilik=ArrayList<Pengguna>()
    private var arrayListPenyewa=ArrayList<Pengguna>()
    private var arrayListMenuChat=ArrayList<MenuChat>()
    private  var arrayListAccount=ArrayList<String>()
    private var arrayListChat=ArrayList<Chat>()
    private var calendar=Calendar.getInstance()
    private var uri:Uri?=null
    private var menuChatId=UUID.randomUUID().toString()

    private lateinit var binding:ActivityChatBinding
    private lateinit var emailPenerima:String
    private lateinit var emailPengirim:String
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter:ChatAdapter
    private lateinit var chat:Chat
    private lateinit var menuChat:MenuChat
    private lateinit var chatIntent: Intent
    private lateinit var tanggalHariIni: String
    private lateinit var namaChatImage:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@ChatActiviity)


        tanggalHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault()).format(calendar.time)

        chatIntent=intent

        emailPenerima= chatIntent.getStringExtra(Constant().KEY_EMAIL_PENERIMA).toString()
        emailPengirim=chatIntent.getStringExtra(Constant().KEY_EMAIL_PENGIRIM).toString()


        if(emailSaatIni==emailPenerima)
        {
            emailPenerima=chatIntent.getStringExtra(Constant().KEY_EMAIL_PENGIRIM).toString()
            emailPengirim=chatIntent.getStringExtra(Constant().KEY_EMAIL_PENERIMA).toString()
        }


        Log.d("email","Pengirim: $emailPengirim, Penerima:$emailPenerima")

        arrayListAccount.add(emailPenerima)
        arrayListAccount.add(emailPengirim)


        checkMenuChatData(object :ChatCallback{
            override fun checkMenuChatData(dataMenuChatDitemukan: Boolean) {

                menuChatDataDitemukan=dataMenuChatDitemukan

                getChatData()

                if(menuChatDataDitemukan)
                {
                    if(emailSaatIni==emailPenerima)
                    {
                        binding.lblusername.text=arrayListMenuChat[0].usernamePengirim

                        storage.child(arrayListMenuChat[0].fotoPengirim).downloadUrl
                            .addOnSuccessListener { uri->
                                Glide.with(this@ChatActiviity)
                                    .load(uri).into(binding.ivfotoprofil)
                            }
                    }

                    else
                    {
                        binding.lblusername.text=arrayListMenuChat[0].usernamePenerima

                        storage.child(arrayListMenuChat[0].fotoPenerima).downloadUrl
                            .addOnSuccessListener { uri->
                                Glide.with(this@ChatActiviity)
                                    .load(uri).into(binding.ivfotoprofil)
                            }
                    }

                }

                else
                {
                    getProfilePengguna()
                }

            }

        })



        binding.btnsendmessage.setOnClickListener {

            if(binding.txtchat.text.isNullOrEmpty())
            {
                Toast.makeText(this@ChatActiviity, "Silahkan Ketik CHat Yang Mau Dikirim", Toast.LENGTH_SHORT).show()
            }

            else
            {
                sendMessageText()
            }
        }


        binding.btnuploadimage.setOnClickListener {
            ImagePicker.with(this@ChatActiviity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent {intent->
                    intentAmbilImageChat.launch(intent)
                }

        }

    }


    private fun sendMessageText()
    {

        if(!menuChatDataDitemukan)
        {

            menuChat=MenuChat(
                idMenuChat=menuChatId,
                emailPenerima = arrayListPemilik[0].email,
                emailPengirim = arrayListPenyewa[0].email,
                fotoPenerima = arrayListPemilik[0].foto,
                fotoPengirim = arrayListPenyewa[0].foto,
                pesanTerakhir = binding.txtchat.text.toString(),
                usernamePenerima = arrayListPemilik[0].username,
                usernamePengirim = arrayListPenyewa[0].username
            )

            database.child(Constant().KEY_MENU_CHAT)
                .child(menuChatId)
                .setValue(menuChat)
        }

        else
        {
            database.child(Constant().KEY_MENU_CHAT)
                .child(arrayListMenuChat[0].idMenuChat)
                .child(Constant().KEY_PESAN_TERAKHIR)
                .setValue(binding.txtchat.text.toString())
        }


        chat=Chat(
            emailPengirim = emailSaatIni,
            emailPenerima = emailPenerima,
            pesan=binding.txtchat.text.toString(),
            tanggal =tanggalHariIni,
            tipe = Constant().KEY_TEXT
        )

        database.child(Constant().KEY_CHAT)
            .push()
            .setValue(chat)

        binding.txtchat.text.clear()

    }


    private var intentAmbilImageChat: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode== RESULT_OK)
        {
            uri= result.data?.data!!

            if(!menuChatDataDitemukan)
            {
                menuChat=MenuChat(
                    idMenuChat=menuChatId,
                    emailPenerima = arrayListPemilik[0].email,
                    emailPengirim = arrayListPenyewa[0].email,
                    fotoPenerima = arrayListPemilik[0].foto,
                    fotoPengirim = arrayListPenyewa[0].foto,
                    pesanTerakhir = "Gambar Diterima",
                    usernamePenerima = arrayListPemilik[0].username,
                    usernamePengirim = arrayListPenyewa[0].username
                )

                database.child(Constant().KEY_MENU_CHAT)
                    .child(menuChatId)
                    .setValue(menuChat)
            }


            else
            {
                database.child(Constant().KEY_MENU_CHAT)
                    .child(arrayListMenuChat[0].idMenuChat)
                    .child(Constant().KEY_PESAN_TERAKHIR)
                    .setValue("Pesan gambar")
            }

            namaChatImage="${Constant().KEY_CHAT_IMAGE}/$menuChatId/${UUID.randomUUID()}"

            chat=Chat(
                emailPengirim=emailPengirim,
                emailPenerima=emailPenerima,
                pesan=namaChatImage,
                tanggal=tanggalHariIni,
                tipe=Constant().KEY_IMAGE
            )

            storage.child(namaChatImage).putFile(uri!!)
                .addOnSuccessListener {
                    database.child(Constant().KEY_CHAT)
                        .push()
                        .setValue(chat)
                }
        }
    }

    private fun checkMenuChatData(chatCallback: ChatCallback)
    {
        chatCallback.checkMenuChatData(false)
        database.child(Constant().KEY_MENU_CHAT)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                      if(snapshot.exists())
                      {
                            snapshot.children.forEach { snap->
                                val snapIdMenuChat=snap.child(Constant().KEY_ID_MENU_CHAT).value.toString()
                                val snapEmailPengirim=snap.child(Constant().KEY_EMAIL_PENGIRIM).value.toString()
                                val snapEmailPenerima=snap.child(Constant().KEY_EMAIL_PENERIMA).value.toString()
                                val snapFotoPengirim=snap.child(Constant().KEY_FOTO_PENGIRIM).value.toString()
                                val snapFotoPenerima=snap.child(Constant().KEY_FOTO_PENERIMA).value.toString()
                                val snapPesanTerakhir=snap.child(Constant().KEY_PESAN_TERAKHIR).value.toString()
                                val snapUsernamePenerima=snap.child(Constant().KEY_USER_PENERIMA).value.toString()
                                val snapUsernamePengirim=snap.child(Constant().KEY_USER_PENGIRIM).value.toString()

                                if(snapEmailPenerima in arrayListAccount && snapEmailPengirim in arrayListAccount)
                                {
                                    menuChat= MenuChat(
                                        idMenuChat =snapIdMenuChat,
                                        emailPenerima = snapEmailPenerima,
                                        emailPengirim = snapEmailPengirim,
                                        fotoPenerima = snapFotoPenerima,
                                        fotoPengirim = snapFotoPengirim,
                                        pesanTerakhir = snapPesanTerakhir,
                                        usernamePenerima = snapUsernamePenerima,
                                        usernamePengirim = snapUsernamePengirim
                                    )
                                    arrayListMenuChat.add(menuChat)

                                    chatCallback.checkMenuChatData(true)
                                }

                            }
                      }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }
            })
    }


    private fun getProfilePengguna()
    {

        database.child(Constant().KEY_USER)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->

                        val snapEmail=snap.child(Constant().KEY_EMAIL).value.toString()
                        val snapUsername=snap.child(Constant().KEY_USERNAME).value.toString()
                        val snapNoTelp=snap.child(Constant().KEY_NOTELP).value.toString()
                        val snapJenisKelamin=snap.child(Constant().KEY_JENIS_KELAMIN).value.toString()
                        val snapFoto=snap.child(Constant().KEY_FOTO).value.toString()
                        val snapId=snap.child(Constant().KEY_ID_PENGGUNA).value.toString()

                        if(snapEmail==emailPenerima)
                        {
                            Log.d("email penerima ditemukan","$emailPenerima $snapEmail")

                            binding.lblusername.text=snapUsername

                            val pemilik=Pengguna(
                                id=snapId,
                                email = snapEmail,
                                foto=snapFoto,
                                jenisKelamin = snapJenisKelamin,
                                noTelp = snapNoTelp,
                                username = snapUsername,
                            )

                            storage.child(snapFoto).downloadUrl
                                .addOnSuccessListener { uri->
                                    Glide.with(this@ChatActiviity)
                                        .load(uri)
                                        .into(binding.ivfotoprofil)
                                }

                            arrayListPemilik.add(pemilik)

                        }

                        else if(snapEmail==emailPengirim)
                        {
                            Log.d("email penerima ditemukan","$emailPengirim $snapEmail")

                            val penyewa=Pengguna(
                                id=snapId,
                                email = snapEmail,
                                foto=snapFoto,
                                jenisKelamin = snapJenisKelamin,
                                noTelp = snapNoTelp,
                                username = snapUsername,
                            )

                            arrayListPenyewa.add(penyewa)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })

    }




    private fun getChatData()
    {
        database.child(Constant().KEY_CHAT)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListChat.clear()
                    snapshot.children.forEach {snap->
                        val snapEmailPengirim=snap.child(Constant().KEY_EMAIL_PENGIRIM).value.toString()
                        val snapEmailPenerima=snap.child(Constant().KEY_EMAIL_PENERIMA).value.toString()
                        val snapEmailPesan=snap.child(Constant().KEY_PESAN).value.toString()
                        val snapTanggal=snap.child(Constant().KEY_TANGGAL).value.toString()
                        val snapTipe=snap.child(Constant().KEY_TYPE).value.toString()

                        if(snapEmailPenerima in arrayListAccount && snapEmailPengirim in arrayListAccount)
                        {
                            chat=Chat(
                                emailPenerima=snapEmailPenerima,
                                emailPengirim = snapEmailPengirim,
                                pesan=snapEmailPesan,
                                tanggal = snapTanggal,
                                tipe = snapTipe
                            )

                            arrayListChat.add(chat)
                        }

                        layoutManager=LinearLayoutManager(this@ChatActiviity)
                        adapter= ChatAdapter(arrayListChat,emailSaatIni)
                        binding.rvchat.layoutManager=layoutManager
                        binding.rvchat.adapter=adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }


}