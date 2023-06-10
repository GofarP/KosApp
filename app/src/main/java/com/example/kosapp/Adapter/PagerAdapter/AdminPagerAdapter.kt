package com.example.kosapp.Adapter.PagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kosapp.Fragment.FragmentAdminKos
import com.example.kosapp.Fragment.AkunFragment
import com.example.kosapp.Fragment.VerifikasiFragment

class AdminPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
       var fragment:Fragment?=null

        when(position)
        {
            0->fragment= VerifikasiFragment()
            1->fragment=VerifikasiKosFragment()
            2->fragment=FragmentAdminKos()
            3->fragment= AkunFragment()
            4->fragment=FrgmnetAdminNotifikasi()
        }

        return fragment as Fragment

    }

}