package com.example.kosapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter.OnClickListener
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentPermintaanBinding
import com.example.kosapp.databinding.LayoutPermintaanBinding
import java.util.*
import kotlin.collections.ArrayList


class PermintaanFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentPermintaanBinding
    private lateinit var adapter:PermintaanAdapter
    private var permintaanArrayList=ArrayList<Permintaan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPermintaanBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addData()

        adapter= PermintaanAdapter(permintaanArrayList, this)
        val layoutManager=LinearLayoutManager(activity)
        binding.rvpermintaan.layoutManager=layoutManager
        binding.rvpermintaan.adapter=adapter
    }

    private fun addData()
    {
        var permintaan=Permintaan(
            id="8912898129892",
            body = "Permintaan Untuk Masuk Kos",
            dari="aaa@email.com",
            kepada="bbb@email.com",
            tanggal=Date()
        )

        permintaanArrayList.add(permintaan)

        permintaan=Permintaan(
            id="8912898129892",
            body = "Permintaan Untuk Keluar Kos",
            dari="aaa@email.com",
            kepada="bbb@email.com",
            tanggal=Date()
        )

        permintaanArrayList.add(permintaan)

        permintaan=Permintaan(
            id="8912898129892",
            body = "Permintaan Untuk Masuk Kos",
            dari="aaa@email.com",
            kepada="ccc@email.com",
            tanggal=Date()
        )

        permintaanArrayList.add(permintaan)

        permintaan=Permintaan(
            id="8912898129892",
            body = "Permintaan Untuk Keluar Kos",
            dari="aaa@email.com",
            kepada="ccc@email.com",
            tanggal=Date()
        )

        permintaanArrayList.add(permintaan)

    }

    override fun onTerimaCLickListener(view: View, dataPermintaan: Permintaan) {
        Toast.makeText(activity, "Menerima...", Toast.LENGTH_SHORT).show()
    }

    override fun onTolakClickListener(view: View, dataPermintaan: Permintaan) {
        Toast.makeText(activity, "Menolak...", Toast.LENGTH_SHORT).show()
    }


}