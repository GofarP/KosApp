package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.History
import com.example.kosapp.databinding.LayoutHistoryBinding

class HistoryAdapter(private val listHistory: ArrayList<History>, )
    :RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{
        class ViewHolder(layoutHistoryBinding: LayoutHistoryBinding)
            :RecyclerView.ViewHolder(layoutHistoryBinding.root)
        {
                val binding=layoutHistoryBinding

                fun bind(dataHistory: History)
                {
                    binding.lbljudulhistory.text=dataHistory.title
                    binding.lblbodyhistory.text=dataHistory.body
                    binding.lbltglhistory.text=dataHistory.tanggal.toString()
                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding=LayoutHistoryBinding.inflate(LayoutInflater.from(parent.context),parent, false )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.bind(listHistory[position])
        }

        override fun getItemCount(): Int {
            return listHistory.size
        }

}