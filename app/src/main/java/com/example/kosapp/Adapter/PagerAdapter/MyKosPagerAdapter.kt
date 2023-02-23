package com.example.kosapp.Adapter.PagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kosapp.Fragment.DisewaFragment
import com.example.kosapp.Fragment.MenyewaFragment
import com.example.kosapp.Fragment.PermintaanFragment

class MyKosPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity)  {

    var fragmentList= listOf(MenyewaFragment(),
        DisewaFragment(),
        PermintaanFragment()
    )

    override fun getItemCount(): Int=fragmentList.size

    override fun createFragment(position: Int)=fragmentList[position]

}