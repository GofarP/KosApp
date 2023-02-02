package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutHomeKosBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomeKosAdapter(private val listKos: ArrayList<Kos>,private val  itemOnClick:ItemOnClick)
    : RecyclerView.Adapter<HomeKosAdapter.ViewHolder>()
{

    class ViewHolder(layoutHomeKosBinding: LayoutHomeKosBinding)
        :RecyclerView.ViewHolder(layoutHomeKosBinding.root)
    {
        private val binding=layoutHomeKosBinding
        private val store= Firebase.storage

        fun bind(dataKos: Kos, itemAdapterCallback: ItemOnClick) {

            itemView.apply {

                binding.lbljeniskos.text=dataKos.jenis
                binding.lblsisa.text= "Sisa:${dataKos.sisa}"
                binding.lblnama.text=dataKos.nama
                binding.lblalamat.text=dataKos.alamat
                binding.lblfasilitas.text=dataKos.fasilitas
                binding.lblharga.text="Rp.${dataKos.biaya}"

                store.reference.child(dataKos.gambarThumbnail)
                    .downloadUrl
                    .addOnSuccessListener {url->
                        Glide.with(this.context)
                            .load(url)
                            .into(binding.ivkos)
                    }
                    .addOnFailureListener {error->
                        binding.lblharga.text=error.message
                    }

                itemView.setOnClickListener{view->
                    itemAdapterCallback.onClick(view, dataKos)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutHomeKosBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listKos[position],itemOnClick)
    }

    override fun getItemCount(): Int {
        return listKos.size
    }

    interface ItemOnClick {
        fun onClick(v: View, dataKos: Kos)
    }
}