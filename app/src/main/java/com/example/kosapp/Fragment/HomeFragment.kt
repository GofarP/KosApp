package com.example.kosapp.Fragment

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.MenuChatActivity
import com.example.kosapp.Adapter.PagerAdapter.HomePagerAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.Interface.sendMessage
import com.example.kosapp.databinding.FragmentHomeBinding
import com.example.kosapp.databinding.FragmentTestBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var testBinding:FragmentTestBinding
    private lateinit var preferenceManager: PreferenceManager


    private var database=Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentHomeBinding.inflate(inflater,container,false)
        testBinding=FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(view.context)


        if(preferenceManagerNotValid())
        {
            getUser()
        }

        else
        {
            binding.lblnamapengguna.text= "Halo  ${preferenceManager.getString(Constant().KEY_USERNAME)}"
        }

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


        binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem= tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        binding.textinputlayout.setEndIconOnClickListener {

            when(val frag=activity?.supportFragmentManager?.findFragmentByTag("f${binding.viewPager.currentItem}"))
            {
                 is SemuaKosFragment->{

                     if(binding.txtsearchkos.text.isNullOrEmpty())
                     {
                         frag.getSemuaDataKos()
                     }

                     else
                     {
                         frag.cariSemuaDataKos(binding.txtsearchkos.text.toString())
                     }
                 }

                is PriaKosFragment->{
                    if(binding.txtsearchkos.text.isNullOrEmpty())
                    {
                        frag.getDataKosPria()
                    }

                    else
                    {
                        frag.cariDataKosPria(binding.txtsearchkos.text.toString())
                    }
                }

                is WanitaKosFragment->{
                    if(binding.txtsearchkos.text.isNullOrEmpty())
                    {
                        frag.getDataKosWanita()
                    }

                    else
                    {
                        frag.cariDataKosWanita(binding.txtsearchkos.text.toString())
                    }
                }

                is CampurKosFragment->{
                    if(binding.txtsearchkos.text.isNullOrEmpty())
                    {
                        frag.getDataKosCampuran()
                    }

                    else
                    {
                        frag.cariDataKosCampuran(binding.txtsearchkos.text.toString())
                    }
                }


            }
        }

        binding.ivmessage.setOnClickListener {
            startActivity(Intent(activity,MenuChatActivity::class.java))
        }

    }

    private fun getUser()
    {
        val userId=FirebaseAuth.getInstance().currentUser?.uid

        database.child(Constant().KEY_USER)
            .child(userId.toString())
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        preferenceManager.putString(Constant().KEY_USERNAME,snapshot.child(Constant().KEY_USERNAME).value.toString())
                        preferenceManager.putString(Constant().KEY_EMAIL,snapshot.child(Constant().KEY_EMAIL).value.toString())
                        preferenceManager.putString(Constant().KEY_JENIS_KELAMIN,snapshot.child(Constant().KEY_JENIS_KELAMIN).value.toString())

                        binding.lblnamapengguna.text="Halo ${snapshot.child(Constant().KEY_USERNAME).value.toString()}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    private fun preferenceManagerNotValid():Boolean
    {
        var notValid=false

        val preferenceManagerList= arrayListOf(
            preferenceManager.getString(Constant().KEY_USERNAME),
            preferenceManager.getString(Constant().KEY_EMAIL),
            preferenceManager.getString(Constant().KEY_JENIS_KELAMIN)
        )

        for(i in preferenceManagerList.indices)
        {
            if (preferenceManagerList[0].isNullOrEmpty()) return true
        }

        return notValid

    }

}