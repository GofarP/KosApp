package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.PermintaanVerifikasiKos
import com.example.kosapp.databinding.LayoutPermintaanVerifikasiBinding


class PermintaanVerifikasiKosAdapter(private val verifikasiArrayList:ArrayList<PermintaanVerifikasiKos>,
                                     private val itemClickListener:OnItemClickListener)
    :RecyclerView.Adapter<PermintaanVerifikasiKosAdapter.ViewHolder>() {

    class ViewHolder(layoutVerifikasiBinding: LayoutPermintaanVerifikasiBinding)
        :RecyclerView.ViewHolder(layoutVerifikasiBinding.root)
    {
            val bind=layoutVerifikasiBinding

            fun bind(permintaanVerifikasiKos: PermintaanVerifikasiKos, itemClickListener:OnItemClickListener){
                itemView.apply {
                    bind.lbltanggal.text=permintaanVerifikasiKos.tanggal
                    bind.lbljudulpermintaan.text=permintaanVerifikasiKos.judul
                    bind.lblisipermintaan.text=permintaanVerifikasiKos.isi
                    bind.btndetailverifikasi.setOnClickListener { view->
                        itemClickListener.onDetailClickListener(view, permintaanVerifikasiKos)
                    }

                    bind.btnterimaverifikasi.setOnClickListener {view->
                        itemClickListener.onTerimaClickListener(view, permintaanVerifikasiKos)
                    }

                    bind.btntolakverifikasi.setOnClickListener {view->
                        itemClickListener.onTolakClickListener(view, permintaanVerifikasiKos)
                    }
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind=LayoutPermintaanVerifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(verifikasiArrayList[position],itemClickListener)
    }

    override fun getItemCount(): Int {
        return verifikasiArrayList.size
    }

    interface OnItemClickListener
    {
        fun onDetailClickListener(view:View, permintaanVerifikasiKos: PermintaanVerifikasiKos)
        fun onTerimaClickListener(view:View, permintaanVerifikasiKos: PermintaanVerifikasiKos)
        fun onTolakClickListener(view:View, permintaanVerifikasiKos: PermintaanVerifikasiKos)
    }
}