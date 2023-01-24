package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailKosSayaActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter.*
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentMenyewaBinding


class MenyewaFragment : Fragment(), ItemOnCLickMenyewa {

    private lateinit var  binding:FragmentMenyewaBinding
    private var kosArrayList=ArrayList<Kos>()
    private lateinit var adapter: MenyewaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMenyewaBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addData()
        adapter= MenyewaAdapter(kosArrayList,this)
        var linearLayout=LinearLayoutManager(activity)
        binding.rvmenyewa.layoutManager=linearLayout
        binding.rvmenyewa.adapter=adapter

    }

    private fun addData()
    {
        var kos= Kos(
            id="12345",
            nama = "Kos Jaya Makmur",
            alamat="Jl. Jalan",
            sisa=3,
            jenis="Laki-Laki",
            gambarThumbnail = "hehe",
            gambarFasilitas = arrayListOf("hehe","hihihi","huhuhu"),
            biaya=300000.00
        )

        kosArrayList.add(kos)

        kos= Kos(
            id="21345",
            nama="Kos Strong n independent",
            jenis = "Perempuan",
            alamat = "Jl.kaki",
            sisa=3,
            gambarThumbnail = "hihi",
            gambarFasilitas =  arrayListOf("hehe","hihihi","huhuhu"),
            biaya = 200000.00
        )

        kosArrayList.add(kos)

        kos= Kos(
            id="321292812",
            nama="Kost Mandiri",
            alamat = "Jl.Kemana",
            sisa=3,
            jenis = "Laki-Laki",
            gambarThumbnail = "huhahuha",
            gambarFasilitas = arrayListOf("hihi","hehe","haha"),
            biaya=100000.00
        )

        kosArrayList.add(kos)
    }

    override fun OnSelengkapnyaClick(v: View, dataKos: Kos) {
      startActivity(Intent(activity, DetailKosSayaActivity::class.java))
    }


}