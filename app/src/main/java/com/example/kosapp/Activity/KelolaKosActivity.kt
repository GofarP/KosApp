package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityKelolaKosBinding

class KelolaKosActivity : AppCompatActivity() {
    private lateinit var  binding:ActivityKelolaKosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKelolaKosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionbar.titleactionbar.text="Kelola Kos"
    }
}