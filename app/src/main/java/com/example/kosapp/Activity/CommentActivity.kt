package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.CommentAdapter
import com.example.kosapp.R
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Comment
import com.example.kosapp.databinding.ActivityCommentBinding
import java.util.*
import kotlin.collections.ArrayList

class CommentActivity : AppCompatActivity() {
    private lateinit var bind:ActivityCommentBinding

    private var commentArrayList=ArrayList<Comment>()
    private lateinit var commentAdapter:CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityCommentBinding.inflate(layoutInflater)
        setContentView(bind.root)

        Helper().setStatusBarColor(this@CommentActivity)

        addData()

        commentAdapter= CommentAdapter(commentArrayList)
        val layoutManager=LinearLayoutManager(this)
        bind.rvcomment.layoutManager=layoutManager
        bind.rvcomment.adapter=commentAdapter


        bind.commentKembali.setOnClickListener {
            super.onBackPressed()
        }

        bind.btnsend.setOnClickListener {
            val komen=bind.txtcomment.text.toString()

            Toast.makeText(this@CommentActivity, komen, Toast.LENGTH_SHORT).show()
        }



    }


    fun addData()
    {
        var comment=Comment(
            idPengguna = UUID.randomUUID().toString(),
            namaPengguna = "Gopro 123",
            gambar="",
            comment = "Kos nya bau kelabang, udah gitu pengap lagi, duh.",
            tanggal = Date()
        )

        commentArrayList.add(comment)


        comment=Comment(
            idPengguna = UUID.randomUUID().toString(),
            namaPengguna = "xxPutraxx",
            gambar="",
            comment = "Kosnya nyaman banget... et tapi boong",
            tanggal = Date()
        )

        commentArrayList.add(comment)

        comment=Comment(
            idPengguna = UUID.randomUUID().toString(),
            namaPengguna = "Perdana aja",
            gambar="",
            comment = "Ya lumayanlah, sesuai dengan harga",
            tanggal = Date()
        )

        commentArrayList.add(comment)
    }


}