package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.databinding.LayoutTransaksiBinding
import com.google.firebase.auth.FirebaseAuth

class TransaksiAdapter(private val listTransaksi: ArrayList<Transaksi>, )
    :RecyclerView.Adapter<TransaksiAdapter.ViewHolder>()
{
        class ViewHolder(layoutTransaksi: LayoutTransaksiBinding)
            :RecyclerView.ViewHolder(layoutTransaksi.root)
        {
                val binding=layoutTransaksi

                fun bind(dataTransaksi: Transaksi)
                {

                    binding.lbljudulhistory.text=dataTransaksi.judul
                    binding.lblbodyhistory.text=dataTransaksi.isi
                    binding.lbltglhistory.text=dataTransaksi.tanggal

                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding=LayoutTransaksiBinding.inflate(LayoutInflater.from(parent.context),parent, false )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.bind(listTransaksi[position])
        }

        override fun getItemCount(): Int {
            return listTransaksi.size
        }

}