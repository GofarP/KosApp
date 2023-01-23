package com.example.kosapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter.ItemOnClick
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentDisewaBinding

class DisewaFragment : Fragment(), ItemOnClick {


    private lateinit var binding: FragmentDisewaBinding
    private lateinit var adapter:DisewaAdapter
    private var kosArrayList=ArrayList<Kos>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDisewaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addData()

        adapter= DisewaAdapter(kosArrayList, this)
        val linearLayoutManager:RecyclerView.LayoutManager=LinearLayoutManager(activity)
        binding.rvdisewa.layoutManager=linearLayoutManager
        binding.rvdisewa.adapter=adapter
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

        kosArrayList.add(kos)

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

        kosArrayList.add(kos)

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

        kosArrayList.add(kos)
    }

    override fun OnEditClick(v: View, dataKos: Kos) {
        Toast.makeText(activity, "mengedit...", Toast.LENGTH_SHORT).show()
    }

    override fun OnDeleteClick(v: View, dataKos: Kos) {
        Toast.makeText(activity, "Menghapus...", Toast.LENGTH_SHORT).show()
    }

    override fun onPeminjamClick(v: View, dataKos: Kos) {
        Toast.makeText(activity, "Detail Peminjam", Toast.LENGTH_SHORT).show()
    }

}