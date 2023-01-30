package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.NegaraAdapter
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity(), NegaraAdapter.RecyclerViewClickListener {
    private lateinit var binding:ActivityTestBinding
    private lateinit var adapter:NegaraAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager

    var kosArrayList=ArrayList<Kos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)

        setContentView(binding.root)


        adapter= NegaraAdapter(kosArrayList)
        layoutManager=LinearLayoutManager(this)
        binding.rvtest.layoutManager=layoutManager
        binding.rvtest.adapter=adapter

    }



    override fun onItemClicked(view: View, kos: Kos) {
        Toast.makeText(this@TestActivity, "Halo", Toast.LENGTH_SHORT).show()
    }

}