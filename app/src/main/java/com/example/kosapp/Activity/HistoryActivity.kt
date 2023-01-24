package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.HistoryAdapter
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.History
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityHistoryBinding
import com.example.kosapp.databinding.LayoutHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHistoryBinding
    private  var historyList=ArrayList<History>()
    private lateinit var adapter:HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper().setStatusBarColor(this@HistoryActivity)
        addData()

        adapter=HistoryAdapter(historyList)
        val layoutManager:RecyclerView.LayoutManager=LinearLayoutManager(this)
        binding.rvhistory.layoutManager=layoutManager
        binding.rvhistory.adapter=adapter
    }

    private fun addData()
    {
        var history=History(
            id = "228398923",
            tipe = "Kelola Kos",
            title="Menambah kos",
            body="Anda Baru Saja Menambah Kosan",
            tanggal = Date()
        )

        historyList.add(history)

         history=History(
            id = "228398923",
            tipe = "Refund",
            title="Pengembalian Uang",
            body="Si anu baru saja mengembalikan uang",
            tanggal = Date()
        )

        historyList.add(history)


         history=History(
            id = "228398923",
            tipe = "Kelola Kos",
            title="Menghapus Kos",
            body="Anda Baru Saja Menghapus Kosan",
            tanggal = Date()
        )

        historyList.add(history)
    }


}