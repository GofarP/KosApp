package com.example.kosapp.Fragment

import android.content.Intent
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kosapp.Activity.MenuChatActivity
import com.example.kosapp.Adapter.PagerAdapter.HomePagerAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point


class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    val userId=FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var locationEngine: LocationEngine
    private lateinit var lokasiSekarang: Location


    private var database=Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager=PreferenceManager()
        preferenceManager.preferenceManager(view.context)

        //ambil data user
        getUser()

        //check Verifikasi Akun
        checkVerifikasi()

        binding.viewPager.adapter=HomePagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, index->
            tab.text=when(index){
                0->{"Semua"}
                1->{"Pria"}
                2->{"Wanita"}
                3->{"Campur"}
                4->{"Terdekat"}
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
                         @SuppressWarnings("MissingPermission")
                         frag.lokasiSekarang=locationEngine.lastLocation
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

                is KosTerdekatFragment->{
                    if (binding.txtsearchkos.text.isNullOrEmpty())
                    {
                        frag.getSemuaKosTerdekat()
                    }

                    else
                    {
                        frag.cariKosTerdekat(binding.txtsearchkos.text.toString())
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

        database.child(Constant().KEY_USER)
            .child(userId.toString())
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        val snapUsername=snapshot.child(Constant().KEY_USERNAME).value.toString()
                        val snapEmail=snapshot.child(Constant().KEY_EMAIL).value.toString()
                        val snapJenisKelamin=snapshot.child(Constant().KEY_JENIS_KELAMIN).value.toString()

                        preferenceManager.putString(Constant().KEY_USERNAME,snapUsername)
                        preferenceManager.putString(Constant().KEY_EMAIL,snapEmail)
                        preferenceManager.putString(Constant().KEY_JENIS_KELAMIN,snapJenisKelamin)

                        binding.lblnamapengguna.text="Halo $snapUsername"

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    private fun checkVerifikasi()
    {
        database.child(Constant().KEY_VERIFIKASI)
            .child(userId.toString())
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val snapVerifikasi=snapshot.child(Constant().KEY_STATUS_VERIFIKASI_AKUN).value.toString()

                    preferenceManager.putString(Constant().KEY_STATUS_VERIFIKASI_AKUN,snapVerifikasi)


                    when (snapVerifikasi) {
                        Constant().KEY_TERVERIFIKASI -> {
                            binding.lblverifikasi.text="Akun Sudah Di Verifikasi"
                            binding.lblverifikasi.setTextColor(ContextCompat.getColor(context!!, R.color.green_ok))
                        }
                        Constant().KEY_PENGAJUAN_VERIFIKASI -> {
                            binding.lblverifikasi.text="Akun Anda Masih Dalam Tahap Pengajuan Verifikasi. \n Harap Ditunggu"
                            binding.lblverifikasi.setTextColor(ContextCompat.getColor(context!!, R.color.green_ok))
                        }
                        Constant().KEY_BELUM_VERIFIKASI -> {
                            binding.lblverifikasi.text="Akun Anda Belum Diverifikasi. \nSilahkan Verifikasi Lewat Menu Pengaturan"
                            binding.lblverifikasi.setTextColor(ContextCompat.getColor(context!!, R.color.red_cancel))
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                  Log.d("snap",error.message)
                }

            })
    }



}