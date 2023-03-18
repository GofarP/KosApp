package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.CommentAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Comment
import com.example.kosapp.databinding.ActivityCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentActivity : AppCompatActivity() {
    private lateinit var bind:ActivityCommentBinding

    private var commentArrayList=ArrayList<Comment>()

    private lateinit var commentAdapter:CommentAdapter

    private var calendar=Calendar.getInstance()

    private lateinit var tanggalHariIni: String

    private lateinit var idKos: String

    private lateinit var emailPemilik: String

    private var database= Firebase.database.reference

    private lateinit var bundle:Bundle

    private lateinit var comment: Comment

    private var emailSaatIni=FirebaseAuth.getInstance().currentUser?.email.toString()

    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    private lateinit var layoutManager:RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityCommentBinding.inflate(layoutInflater)
        setContentView(bind.root)

        tanggalHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        Helper().setStatusBarColor(this@CommentActivity)

        bundle= intent.extras!!

        idKos=bundle.getString(Constant().KEY_ID_KOS).toString()
        emailPemilik=bundle.getString(Constant().KEY_EMAIL_PEMILIK).toString()

        checkHistoryKos(idKos)

        getComment()


        bind.commentKembali.setOnClickListener {
            super.onBackPressed()
        }

        bind.btnsend.setOnClickListener {

            database.child(Constant().KEY_USER)
                .child(idPengguna)
                .get()
                .addOnSuccessListener {snap->
                    comment= Comment(
                        email = emailSaatIni,
                        foto=snap.child(Constant().KEY_FOTO).value.toString(),
                        idComment = UUID.randomUUID().toString(),
                        isiComment = bind.txtcomment.text.toString(),
                        tanggal = tanggalHariIni,
                        username = snap.child(Constant().KEY_USERNAME).value.toString()
                    )

                    sendComment(comment,idKos)
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Gagal Mengirim Komentar", Toast.LENGTH_SHORT).show()
                }
        }


    }


    private fun data()
    {
        comment= Comment(
            email=emailSaatIni,
            foto = "",
            idComment = UUID.randomUUID().toString(),
            isiComment = "Ini Sebuah Comment",
            tanggal=tanggalHariIni,
            username = "Putra"
        )

        commentArrayList.add(comment)

        layoutManager=LinearLayoutManager(this)
        commentAdapter= CommentAdapter(commentArrayList)
        bind.rvcomment.layoutManager=layoutManager
        bind.rvcomment.adapter=commentAdapter
    }

    private fun checkHistoryKos(idKos:String)
    {
        database.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.child(Constant().KEY_HISTORY).exists())
               {

                   var historyDitemukan=false
                    snapshot.child(Constant().KEY_HISTORY)
                        .child(emailSaatIni.replace(".",","))
                        .children.forEach {snap->
                            val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                            if(snapIdKos==idKos)
                            {
                                historyDitemukan=true
                                bind.btnsend.visibility=View.VISIBLE
                                bind.txtcomment.visibility=View.VISIBLE
                            }
                        }

                    if(historyDitemukan || emailPemilik == emailSaatIni)
                    {
                        bind.btnsend.visibility=View.VISIBLE
                        bind.txtcomment.visibility=View.VISIBLE
                    }

               }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db error",error.message)
            }

        })
    }

    private fun getComment()
    {
        database.child(Constant().KEY_COMMENT)
            .child(idKos)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentArrayList.clear()
                    snapshot.children.forEach {snap->

                        comment=Comment(
                            email=snap.child(Constant().KEY_EMAIL).value.toString(),
                            foto=snap.child(Constant().KEY_FOTO).value.toString(),
                            idComment = snap.child(Constant().KEY_ID_COMMENT).value.toString(),
                            isiComment = snap.child(Constant().KEY_ISI_COMMENT).value.toString(),
                            tanggal = snap.child(Constant().KEY_TANGGAL).value.toString(),
                            username = snap.child(Constant().KEY_USERNAME).value.toString()
                        )

                        commentArrayList.add(comment)
                    }

                    commentAdapter= CommentAdapter(commentArrayList)
                    layoutManager=LinearLayoutManager(this@CommentActivity)
                    bind.rvcomment.layoutManager=layoutManager
                    bind.rvcomment.adapter=commentAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error", error.message.toString())
                }

            })
    }

   private fun sendComment(commentParam: Comment, idKos: String)
   {
        comment= Comment(
            foto = commentParam.foto,
            idComment = UUID.randomUUID().toString(),
            email = emailSaatIni,
            isiComment = commentParam.isiComment,
            tanggal=tanggalHariIni,
            username = commentParam.username
        )
        database.child(Constant().KEY_COMMENT)
            .child(idKos)
            .push()
            .setValue(comment)
            .addOnSuccessListener {
                bind.txtcomment.text.clear()
            }
   }


}