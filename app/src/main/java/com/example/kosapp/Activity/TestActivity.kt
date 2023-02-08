package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.NegaraAdapter
import com.example.kosapp.Callback.SewaKosCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.ActivityTestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TestActivity : AppCompatActivity(), NegaraAdapter.RecyclerViewClickListener {
    private lateinit var binding:ActivityTestBinding
    private lateinit var adapter:NegaraAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private val database=Firebase.database.reference
    private val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()

    var kosArrayList=ArrayList<Kos>()
    var priaAda=false
    var emailAda=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.btntest.setOnClickListener {
            if(priaAda)
            {
                Toast.makeText(this@TestActivity, "pria Ditemukan", Toast.LENGTH_SHORT).show()
            }

            else if(emailAda)
            {
                Toast.makeText(this@TestActivity, "email Ditemukan", Toast.LENGTH_SHORT).show()
            }

            else
            {
                Toast.makeText(this@TestActivity, "Tidak Ada", Toast.LENGTH_SHORT).show()
            }
        }

    }



    override fun onItemClicked(view: View, kos: Kos) {
        if(emailAda)
        {
            Toast.makeText(this@TestActivity, "Ada", Toast.LENGTH_SHORT).show()
        }

        else
        {
            Toast.makeText(this@TestActivity, "Gak Ada", Toast.LENGTH_SHORT).show()
        }
    }


}