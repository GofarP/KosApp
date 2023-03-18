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
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter.ItemOnClick
import com.example.kosapp.Callback.TestCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityRouteJalanBinding
import com.example.kosapp.databinding.FragmentSemuaKosBinding
import com.example.kosapp.databinding.FragmentTestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class TestFragment : Fragment(),HomeKosAdapter.ItemOnClick {

//    private lateinit var binding: FragmentTestBinding
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var homeKosAdapter: HomeKosAdapter
    private lateinit var kos:Kos

    private  var database=Firebase.database.reference
    private var kosArrayList=ArrayList<Kos>()

    private val binding by lazy{
        FragmentTestBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )=binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onClick(v: View, dataKos: Kos) {
        TODO("Not yet implemented")
    }





}