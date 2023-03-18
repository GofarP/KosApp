package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.HistoryKosAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.History
import com.example.kosapp.databinding.ActivityHistoryKosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class HistoryKosActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHistoryKosBinding
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var adapter: HistoryKosAdapter
    private lateinit var history: History

    private val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private val database=FirebaseDatabase.getInstance().reference
    private var historyKosArrayList=ArrayList<History>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHistoryKosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@HistoryKosActivity)
        getData()
    }

    private fun getData()
    {
        database.child(Constant().KEY_HISTORY)
            .child(emailPengguna.replace(".",","))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    historyKosArrayList.clear()
                    snapshot.children.forEach {snapshotHistory->
                        history= History(
                            idHistory = UUID.randomUUID().toString(),
                            idKos= snapshotHistory.child(Constant().KEY_ID_KOS).value.toString(),
                            tanggal = snapshotHistory.child(Constant().KEY_TANGGAL).value.toString(),
                            alamat = snapshotHistory.child(Constant().KEY_ALAMAT_KOS).value.toString(),
                            nama=snapshotHistory.child(Constant().KEY_NAMA_KOS).value.toString(),
                            thumbnailKos = snapshotHistory.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString(),
                        )
                        historyKosArrayList.add(history)
                    }

                    layoutManager=LinearLayoutManager(this@HistoryKosActivity)
                    adapter=HistoryKosAdapter(historyKosArrayList)
                    binding.rvhistorykos.layoutManager=layoutManager
                    binding.rvhistorykos.adapter=adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }
            })
    }



}