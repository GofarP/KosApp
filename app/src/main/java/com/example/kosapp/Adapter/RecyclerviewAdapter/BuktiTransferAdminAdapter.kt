package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.BiayaBukaKos
import com.example.kosapp.databinding.LayoutAdminBuktiTransferBinding

class BuktiTransferAdminAdapter(private val arrayListBiayaBukaKos:ArrayList<BiayaBukaKos>,
                                private val itemClickLihatBukti:ItemOnCLickLihatBukti):
    RecyclerView.Adapter<BuktiTransferAdminAdapter.ViewHolder>() {

        class ViewHolder(layoutAdminBuktiTransferBinding: LayoutAdminBuktiTransferBinding)
            :RecyclerView.ViewHolder(layoutAdminBuktiTransferBinding.root)
            {

                private val binding=layoutAdminBuktiTransferBinding

                fun bind(biayaBukaKos:BiayaBukaKos, itemOnCLickLihatBukti:ItemOnCLickLihatBukti){
                    itemView.apply {
                        binding.lblnamakos.text=biayaBukaKos.namaKos

                        binding.buttonbuktipembayaran.setOnClickListener {view->
                            itemOnCLickLihatBukti.onDetailBuktiPembayaran(view,biayaBukaKos)
                        }
                    }
                }


            }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding=LayoutAdminBuktiTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayListBiayaBukaKos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayListBiayaBukaKos[position], itemClickLihatBukti)
    }

    interface ItemOnCLickLihatBukti {
        fun onDetailBuktiPembayaran(v: View, biayaBukaKos:BiayaBukaKos)
    }

}