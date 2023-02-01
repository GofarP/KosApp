package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.HistoryAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.History
import com.example.kosapp.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHistoryBinding
    private  var historyList=ArrayList<History>()
    private lateinit var adapter:HistoryAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager

    private var database=Firebase.database.reference
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var history:History

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@HistoryActivity)

        getDataHistory()
        adapter=HistoryAdapter(historyList)
        val layoutManager:RecyclerView.LayoutManager=LinearLayoutManager(this)
        binding.rvhistory.layoutManager=layoutManager
        binding.rvhistory.adapter=adapter
    }

    private fun getDataHistory()
    {
        database.child(Constant().HISTORY)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach { snap->
                        val childDari=snap.child(Constant().DARI).value.toString()
                        val childKepada=snap.child(Constant().KEPADA).value.toString()

                        if(childDari==emailPengguna || childKepada==emailPengguna)
                        {
                            history= History(
                                historyId=snap.child(Constant().ID_HISTORY).value.toString(),
                                dari = childDari,
                                kepada = childKepada,
                                judul=snap.child(Constant().JUDUL).value.toString(),
                                isi = snap.child(Constant().ISI).value.toString(),
                                tipe = snap.child(Constant().TYPE).value.toString(),
                                tanggal = Date()
                            )

                            historyList.add(history)
                        }

                        adapter=HistoryAdapter(historyList)
                        layoutManager=LinearLayoutManager(this@HistoryActivity)
                        binding.rvhistory.layoutManager=layoutManager
                        binding.rvhistory.adapter=adapter


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}