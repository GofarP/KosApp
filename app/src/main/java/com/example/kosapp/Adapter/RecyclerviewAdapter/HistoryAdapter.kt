package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.History
import com.example.kosapp.databinding.LayoutHistoryBinding
import com.google.firebase.auth.FirebaseAuth

class HistoryAdapter(private val listHistory: ArrayList<History>, )
    :RecyclerView.Adapter<HistoryAdapter.ViewHolder>()
{
        class ViewHolder(layoutHistoryBinding: LayoutHistoryBinding)
            :RecyclerView.ViewHolder(layoutHistoryBinding.root)
        {
                val binding=layoutHistoryBinding
                var judulHistory:String?=null
                var isiHistory:String?=null
                val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()

                fun bind(dataHistory: History)
                {

                    judulHistory=dataHistory.judul

                    if(dataHistory.dari==emailPengguna && dataHistory.tipe==Constant().TOLAK_SEWA)
                    {
                       isiHistory="Anda Menolak Permintaan Kos ${dataHistory.kepada}"
                    }
                    else if(dataHistory.dari==emailPengguna && dataHistory.tipe==Constant().BATAL_SEWA)
                    {
                        isiHistory="Anda Membatalkan Permintaan Kos"
                    }
                    else
                    {
                        isiHistory="${dataHistory.isi} oleh ${dataHistory.dari}"
                    }

                    binding.lbljudulhistory.text=judulHistory
                    binding.lblbodyhistory.text=isiHistory
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