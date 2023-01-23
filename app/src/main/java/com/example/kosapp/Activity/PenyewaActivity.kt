package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityPenyewaBinding

class PenyewaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPenyewaBinding
    private var peminjamArrayList=ArrayList<Pengguna>()
    private lateinit var adapter: PenyewaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPenyewaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeLayoutDetail.titleactionbar.text="Daftar Penyewa"
    }



    fun addData()
    {

    }

}