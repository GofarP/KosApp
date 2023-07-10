package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Activity.HistoryKosActivity
import com.example.kosapp.Activity.TransaksiActivity
import com.example.kosapp.Activity.KosSayaActivity
import com.example.kosapp.Activity.TambahKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.MyKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MyKosAdapter.ItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.databinding.FragmentMyKosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyKosFragment : Fragment(), ItemOnClick {


    private lateinit var binding: FragmentMyKosBinding
    private lateinit var adapter:MyKosAdapter
    private lateinit var preferenceManager:PreferenceManager
    private lateinit var statusAkun:String

    private var myKosArrayList=ArrayList<String>()
    private var database=FirebaseDatabase.getInstance().reference

    private val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()



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

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(requireActivity())
        statusAkun=preferenceManager.getString(Constant().KEY_STATUS_VERIFIKASI_AKUN).toString()
        addData()
        adapter= MyKosAdapter(myKosArrayList,this)
        binding.rvmykos.layoutManager=LinearLayoutManager(activity)
        binding.rvmykos.adapter=adapter

    }

    override fun onClick(view: View, myKos: String) {
       when(myKos)
       {
           "Buka Kos-Kosan"->{
               if(statusAkun==Constant().KEY_BELUM_VERIFIKASI || statusAkun==Constant().KEY_PENGAJUAN_VERIFIKASI)
               {
                   Toast.makeText(activity, "Tidak Bisa Menambahkan Kos-Kosa Sebelum Akun Terverifikasi", Toast.LENGTH_SHORT).show()
               }

               else
               {
                   startActivity(Intent(activity, TambahKosActivity::class.java))
               }
           }

           "Kos-Kosan Saya"-> {
                startActivity(Intent(activity, KosSayaActivity::class.java))
           }

           "History Kos Saya"->{
                startActivity(Intent(activity,HistoryKosActivity::class.java))
           }

           "Informasi"->{
               startActivity(Intent(activity, TransaksiActivity::class.java))
           }

       }
    }


    private fun addData()
    {
        myKosArrayList.add("Buka Kos-Kosan")
        myKosArrayList.add("Kos-Kosan Saya")
        myKosArrayList.add("History Kos Saya")
        myKosArrayList.add("Informasi")
    }

}