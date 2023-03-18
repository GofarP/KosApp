package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.ViewGroup
import com.example.kosapp.Model.Kos
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.databinding.LayoutKosDisewaBinding


class DaftarKosAdapter(private val kosArrayList: ArrayList<Kos>)
    :RecyclerView.Adapter<DaftarKosAdapter.ViewHolder>() {


    class ViewHolder(layoutKosDisewaBinding: LayoutKosDisewaBinding)
        :RecyclerView.ViewHolder(layoutKosDisewaBinding.root)
    {
        val binding=layoutKosDisewaBinding

        fun bind()
        {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return kosArrayList.size
    }

    interface OnClickListener
    {
        fun onDetailClickListener()
    }
}