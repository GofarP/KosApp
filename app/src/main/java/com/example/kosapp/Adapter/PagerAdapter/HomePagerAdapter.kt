package com.example.kosapp.Adapter.PagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kosapp.Fragment.*

class HomePagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment?=null
        when(position)
        {
            0->fragment=SemuaKosFragment()
            1->fragment=PriaKosFragment()
            2->fragment=WanitaKosFragment()
            3->fragment=CampurKosFragment()
            4->fragment=KosTerdekatFragment()
        }

        return fragment as Fragment
    }
}