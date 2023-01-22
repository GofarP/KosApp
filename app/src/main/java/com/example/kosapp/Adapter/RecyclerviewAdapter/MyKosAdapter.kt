package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.kosapp.databinding.FragmentMyKosBinding
import com.example.kosapp.databinding.LayoutMykosBinding

class MyKosAdapter(private val myKosList:ArrayList<String>,private val itemOnClick: ItemOnClick)
    : Adapter<MyKosAdapter.ViewHolder>() {


    class ViewHolder(myKosBinding:LayoutMykosBinding)
        :RecyclerView.ViewHolder(myKosBinding.root)
    {
            private val binding=myKosBinding

            fun bind(myKos:String, itemOnClick: ItemOnClick)
            {
                itemView.apply {
                    binding.lblmykos.text=myKos
                }

                itemView.setOnClickListener {
                    itemOnClick.onClick(itemView,myKos)
                }

            }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutMykosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myKosList[position],itemOnClick)
    }

    override fun getItemCount(): Int {

        return myKosList.size
    }


    interface  ItemOnClick {
        fun onClick(view: View, myKos:String)
    }
}