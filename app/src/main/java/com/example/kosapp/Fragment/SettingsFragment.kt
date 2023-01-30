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
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter.ItemOnClick
import com.example.kosapp.Activity.ProfileActivity
import com.example.kosapp.Activity.SigninActivity
import com.example.kosapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class SettingsFragment : Fragment(), ItemOnClick {

    private var settingList=ArrayList<String>()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var adapter:SettingsAdapter
    private var auth=FirebaseAuth.getInstance().currentUser
    private var storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addSettings()
        getDataProfile()

    }

    private fun addSettings()
    {
        settingList.add("Profile")
        settingList.add("Logout")
        adapter= SettingsAdapter(settingList,this)
        binding.rvsettings.layoutManager=LinearLayoutManager(activity)
        binding.rvsettings.adapter=adapter

    }

    private fun  getDataProfile()
    {
        val userId=auth?.uid

        database.child("user")
            .orderByChild("id")
            .equalTo(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {snap->
                    var imagePath=snap.child("foto").value.toString()
                    binding.lblemail.text=snap.child("email").value.toString()
                    binding.lblusername.text=snap.child("username").value.toString()

                    storage.child(imagePath)
                        .downloadUrl
                        .addOnSuccessListener {url->
                            Glide.with(this@SettingsFragment)
                                .load(url)
                                .into(binding.ivsettings)

                        }
                        .addOnFailureListener {error->
                            Toast.makeText(activity, userId.toString(), Toast.LENGTH_SHORT).show()
                        }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onClick(view: View, settingsName: String) {
        when(settingsName)
        {
            "Profile"->{
                startActivity(Intent(activity, ProfileActivity::class.java))
            }

            "Logout"->{
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(activity, "Sukses Logout", Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity,SigninActivity::class.java))
                activity?.finish()
            }
        }
    }

}