package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.CommentAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Comment
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityCommentBinding
import com.example.kosapp.databinding.LayoutBreakdownRatingBinding
import com.example.kosapp.databinding.LayoutRatingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentActivity : AppCompatActivity() {
    private lateinit var bind:ActivityCommentBinding

    private lateinit var commentAdapter:CommentAdapter

    private lateinit var tanggalHariIni: String

    private lateinit var idKos: String

    private lateinit var emailPemilik: String

    private lateinit var bundle:Bundle

    private lateinit var comment: Comment

    private lateinit var layoutManager:RecyclerView.LayoutManager

    private var commentArrayList=ArrayList<Comment>()

    private var calendar=Calendar.getInstance()

    private var database= Firebase.database.reference

    private var emailSaatIni=FirebaseAuth.getInstance().currentUser?.email.toString()

    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var hashMapRating= mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0)

    private var jumlahRatingSekarang=0


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

            val komen=bind.txtcomment.text.toString()

            if(komen.isNullOrEmpty())
            {
                Toast.makeText(this@CommentActivity, "Silahkan Masukkan Komen Yang Ingin Dikirim", Toast.LENGTH_SHORT).show()
            }

            else
            {
                kirimKomen(bind.txtcomment.text.toString())
            }
        }

        bind.ivrating.setOnClickListener {

            val dialogView=layoutInflater.inflate(R.layout.layout_breakdown_rating, null)
            val customDialog= AlertDialog.Builder(this)
                .setView(dialogView)
                .show()
            val customDialogBinding= LayoutBreakdownRatingBinding.inflate(layoutInflater)
            customDialog.setContentView(customDialogBinding.root)

            database.child(Constant().KEY_RATING)
                .child(idKos)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { snap->

                            val rating=snap.child(Constant().KEY_RATING).value.toString().toInt()

                            jumlahRatingSekarang=hashMapRating[rating]?:0
                            hashMapRating[rating]=jumlahRatingSekarang+1

                            customDialogBinding.lblrating1.text="${hashMapRating[1]} Orang"
                            customDialogBinding.lblrating2.text="${hashMapRating[2]} Orang"
                            customDialogBinding.lblrating3.text="${hashMapRating[3]} Orang"
                            customDialogBinding.lblrating4.text="${hashMapRating[4]} Orang"
                            customDialogBinding.lblrating5.text="${hashMapRating[5]} Orang"

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("db error",error.message)
                    }

                })
        }
    }



    private fun kirimKomen(komen:String)
    {
        database.child(Constant().KEY_USER)
            .child(idPengguna)
            .get()
            .addOnSuccessListener {snap->
                comment= Comment(
                    email = emailSaatIni,
                    foto=snap.child(Constant().KEY_FOTO).value.toString(),
                    idComment = UUID.randomUUID().toString(),
                    isiComment = komen,
                    tanggal = tanggalHariIni,
                    username = snap.child(Constant().KEY_USERNAME).value.toString()
                )

                sendComment(comment,idKos)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Gagal Mengirim Komentar", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkHistoryKos(idKos:String)
    {
        if(emailPemilik == emailSaatIni)
        {
            bind.btnsend.visibility=View.VISIBLE
            bind.txtcomment.visibility=View.VISIBLE
        }

        database.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.child(Constant().KEY_HISTORY).exists())
               {

                    snapshot.child(Constant().KEY_HISTORY)
                        .child(emailSaatIni.replace(".",","))
                        .children.forEach {snap->

                            val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()

                            if(snapIdKos==idKos)
                            {
                                bind.btnsend.visibility=View.VISIBLE
                                bind.txtcomment.visibility=View.VISIBLE
                            }
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