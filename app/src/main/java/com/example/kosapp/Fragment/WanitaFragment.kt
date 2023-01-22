package com.example.kosapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentWanitaBinding


class WanitaFragment : Fragment(), ItemOnClick {

    private var kosArrayList=ArrayList<Kos>()
    private lateinit var adapter: HomeKosAdapter
    private lateinit var binding: FragmentWanitaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentWanitaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addData()
        adapter=HomeKosAdapter(kosArrayList,this)
        val linearLayoutManager=LinearLayoutManager(activity)
        binding.rvkoswanita.layoutManager=linearLayoutManager
        binding.rvkoswanita.adapter=adapter
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

    override fun onClick(v: View, dataKos: Kos) {
        Toast.makeText(activity, "Halo Kak", Toast.LENGTH_SHORT).show()
    }


}