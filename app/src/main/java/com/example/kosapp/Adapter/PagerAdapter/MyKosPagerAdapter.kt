package com.example.kosapp.Adapter.PagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kosapp.Fragment.DisewaFragment
import com.example.kosapp.Fragment.MenyewaFragment

class MyKosPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity)  {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment?=null
        when(position)
        {
            0->fragment=MenyewaFragment()
            1->fragment=DisewaFragment()
        }

        return fragment as Fragment
    }

}