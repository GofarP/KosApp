package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutHistoryKosBinding
import com.google.firebase.storage.FirebaseStorage

class HistoryKosAdapter(val historyKosList:ArrayList<Kos>)
    :RecyclerView.Adapter<HistoryKosAdapter.ViewHolder>() {

    class ViewHolder(layoutHistoryKosBinding: LayoutHistoryKosBinding)
        :RecyclerView.ViewHolder(layoutHistoryKosBinding.root)
    {

            val binding=layoutHistoryKosBinding
            var namaKos:String?=null
            var alamatKos:String?=null
            var tanggalSewa:String?=null
            val storage=FirebaseStorage.getInstance().reference

            fun bind(dataKos: Kos)
            {

                itemView.apply {
                    namaKos=dataKos.nama
                    alamatKos=dataKos.alamat

                    storage.downloadUrl.addOnSuccessListener { uri->
                        Glide.with(context)
                            .load(uri)
                            .into(binding.ivkos)
                    }

                }

            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}