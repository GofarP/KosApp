package com.example.kosapp.Fragment

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.kosapp.Activity.MenuChatActivity
import com.example.kosapp.Adapter.PagerAdapter.HomePagerAdapter
import com.example.kosapp.Helper.Helper
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ambilNamaPengguna()

        binding.viewPager.adapter=HomePagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, index->
            tab.text=when(index){
                0->{"Semua"}
                1->{"Pria"}
                2->{"Wanita"}
                3->{"Campur"}


                else->{throw Resources.NotFoundException("Posisi Tidak DItemukan")}
            }
        }.attach()

        binding.textinputlayout.setEndIconOnClickListener {
            Toast.makeText(activity, "Sedang Mencari Kos...", Toast.LENGTH_SHORT).show()
        }

        binding.ivmessage.setOnClickListener {
            startActivity(Intent(activity,MenuChatActivity::class.java))
        }

    }

    fun ambilNamaPengguna()
    {
        val email=FirebaseAuth.getInstance().currentUser?.email
        FirebaseFirestore.getInstance()
            .collection("pengguna")
            .whereEqualTo("email",email)
            .get()
            .addOnSuccessListener {result->
                binding.lblnamapengguna.text=" Halo ${result.documents[0].data?.get("username").toString()}"

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Gagal Mengambil Nama", Toast.LENGTH_SHORT).show()
            }

    }
}