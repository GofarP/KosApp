package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailSewaKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentSemuaKosBinding


class SemuaKosFragment : Fragment(), ItemOnClick {

    private lateinit var binding:FragmentSemuaKosBinding
    private  var kosArraList=ArrayList<Kos>()
    private var homeKosAdapter:HomeKosAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSemuaKosBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addData()
        homeKosAdapter= HomeKosAdapter(kosArraList, this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvkossemua.layoutManager=layoutManager
        binding.rvkossemua.adapter=homeKosAdapter
    }

    private fun addData()
    {
        var kos=Kos(
            id="12345",
            nama = "Kos Jaya Makmur",
            alamat="Jl. Jalan",
            sisa=3,
            jenis="Laki-Laki",
            gambarThumbnail = "hehe",
            gambarFasilitas = arrayListOf("hehe","hihihi","huhuhu"),
            biaya=300000.00
        )

        kosArraList.add(kos)

        kos=Kos(
            id="21345",
            nama="Kos Strong n independent",
            jenis = "Perempuan",
            alamat = "Jl.kaki",
            sisa=3,
            gambarThumbnail = "hihi",
            gambarFasilitas =  arrayListOf("hehe","hihihi","huhuhu"),
            biaya = 200000.00
        )

        kosArraList.add(kos)

        kos=Kos(
            id="321292812",
            nama="Kost Mandiri",
            alamat = "Jl.Kemana",
            sisa=3,
            jenis = "Laki-Laki",
            gambarThumbnail = "huhahuha",
            gambarFasilitas = arrayListOf("hihi","hehe","haha"),
            biaya=100000.00
        )

        kosArraList.add(kos)
    }

    override fun onClick(v: View, dataKos: Kos) {
        startActivity(Intent(activity, DetailSewaKosActivity::class.java))
    }

}