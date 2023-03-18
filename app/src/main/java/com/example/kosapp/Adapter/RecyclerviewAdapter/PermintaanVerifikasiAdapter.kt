package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.databinding.LayoutPermintaanVerifikasiBinding


class PermintaanVerifikasiAdapter(private val verifikasiArrayList:ArrayList<PermintaanVerifikasi>,
                                  private val itemClickListener:OnItemClickListener)
    :RecyclerView.Adapter<PermintaanVerifikasiAdapter.ViewHolder>() {

    class ViewHolder(layoutVerifikasiBinding: LayoutPermintaanVerifikasiBinding)
        :RecyclerView.ViewHolder(layoutVerifikasiBinding.root)
    {
            val bind=layoutVerifikasiBinding

            fun bind(permintaanVerifikasi: PermintaanVerifikasi, itemClickListener:OnItemClickListener){
                itemView.apply {
                    bind.lbltanggal.text=permintaanVerifikasi.tanggal
                    bind.lbljudulpermintaan.text=permintaanVerifikasi.judul
                    bind.lblisipermintaan.text=permintaanVerifikasi.isi
                    bind.btndetailverifikasi.setOnClickListener { view->
                        itemClickListener.onDetailClickListener(view, permintaanVerifikasi)
                    }

                    bind.btnterimaverifikasi.setOnClickListener {view->
                        itemClickListener.onTerimaClickListener(view, permintaanVerifikasi)
                    }

                    bind.btntolakverifikasi.setOnClickListener {view->
                        itemClickListener.onTolakClickListener(view, permintaanVerifikasi)
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
        fun onDetailClickListener(view:View, permintaanVerifikasi: PermintaanVerifikasi)
        fun onTerimaClickListener(view:View, permintaanVerifikasi: PermintaanVerifikasi)
        fun onTolakClickListener(view:View, permintaanVerifikasi: PermintaanVerifikasi)
    }
}