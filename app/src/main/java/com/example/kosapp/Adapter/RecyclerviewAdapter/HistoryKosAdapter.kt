package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.LayoutHistoryKosBinding
import com.google.firebase.storage.FirebaseStorage

class HistoryKosAdapter(val historyKosList:ArrayList<History>)
    :RecyclerView.Adapter<HistoryKosAdapter.ViewHolder>() {

    class ViewHolder(layoutHistoryKosBinding: LayoutHistoryKosBinding)
        :RecyclerView.ViewHolder(layoutHistoryKosBinding.root)
    {

            val binding=layoutHistoryKosBinding
            var namaKos:String?=null
            var alamatKos:String?=null
            var tanggalSewa:String?=null
            val storage=FirebaseStorage.getInstance().reference

            fun bind(dataHistory: History)
            {

                itemView.apply {


                    binding.lblnama.text=dataHistory.nama
                    binding.lblalamat.text=dataHistory.alamat
                    binding.lbltanggal.text=dataHistory.tanggal

                    storage.child(dataHistory.thumbnailKos).downloadUrl.addOnSuccessListener { uri->
                        Glide.with(context)
                            .load(uri)
                            .into(binding.ivkos)
                    }

                }

            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding=LayoutHistoryKosBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(historyKosList[position])
    }

    override fun getItemCount(): Int {
       return historyKosList.size
    }
}