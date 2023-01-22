package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter.ItemOnClick
import com.example.kosapp.Activity.ProfileActivity
import com.example.kosapp.Activity.SigninActivity
import com.example.kosapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment(), ItemOnClick {

    private var settingList=ArrayList<String>()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var adapter:SettingsAdapter
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
        adapter= SettingsAdapter(settingList,this)
        binding.rvsettings.layoutManager=LinearLayoutManager(activity)
        binding.rvsettings.adapter=adapter

    }

    private fun addSettings()
    {
        settingList.add("Profile")
        settingList.add("Logout")
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