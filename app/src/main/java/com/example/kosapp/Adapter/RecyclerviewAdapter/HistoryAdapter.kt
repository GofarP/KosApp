package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutHistoryBinding

class HistoryAdapter(private val listKos: ArrayList<History>, )
    :RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{
        class ViewHolder(layoutHistoryBinding: LayoutHistoryBinding)
            :RecyclerView.ViewHolder(layoutHistoryBinding.root)
        {
                val binding=layoutHistoryBinding

                fun bind()
                {

                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding=LayoutHistoryBinding.inflate(LayoutInflater.from(parent.context),parent, false )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            return listKos.size
        }
}