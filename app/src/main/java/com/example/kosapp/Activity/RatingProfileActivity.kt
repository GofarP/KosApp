package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.RatingProfileAdapter
import com.example.kosapp.Model.Rating
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityRatingProfileBinding
import com.example.kosapp.databinding.LayoutRatingProfileBinding
import com.google.firebase.auth.FirebaseAuth

class RatingProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingProfileBinding
    private lateinit var layoutManager:LayoutManager
    private lateinit var adapter:RatingProfileAdapter
    private lateinit var rating:Rating
    private var profileRatingArrayList=ArrayList<Rating>()
    private var auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRatingProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
    }

    private fun getData()
    {
        rating=
            Rating(idPengguna = "12345", idKos="54321", namaKos = "Kos Absi Jaya","Sangat Baik", "20-02-2023")
        profileRatingArrayList.add(rating)

        layoutManager=LinearLayoutManager(this@RatingProfileActivity)
        adapter= RatingProfileAdapter(ratingArrayList = profileRatingArrayList )
        binding.rvratingprofile.layoutManager=layoutManager
        binding.rvratingprofile.adapter=adapter

    }


}