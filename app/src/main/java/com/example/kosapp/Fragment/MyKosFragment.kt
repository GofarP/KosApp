package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Activity.KosSayaActivity
import com.example.kosapp.Activity.TambahKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.MyKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MyKosAdapter.ItemOnClick
import com.example.kosapp.databinding.FragmentMyKosBinding


class MyKosFragment : Fragment(), ItemOnClick {


    private lateinit var binding: FragmentMyKosBinding
    private var myKosArrayList=ArrayList<String>()
    private lateinit var adapter:MyKosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyKosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addData()
        adapter= MyKosAdapter(myKosArrayList,this)
        binding.rvmykos.layoutManager=LinearLayoutManager(activity)
        binding.rvmykos.adapter=adapter
    }

    override fun onClick(view: View, myKos: String) {
       when(myKos)
       {
           "Buat Kos-Kosan"->{
                startActivity(Intent(activity, TambahKosActivity::class.java))
           }

           "Kos-Kosan Saya"-> {
                startActivity(Intent(activity, KosSayaActivity::class.java))
           }
       }
    }


    private fun addData()
    {
        myKosArrayList.add("Buat Kos-Kosan")
        myKosArrayList.add("Kos-Kosan Saya")
    }

}