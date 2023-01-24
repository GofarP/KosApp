package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.databinding.LayoutPenyewaBinding

class PenyewaAdapter(private val arrayListPenyewa:ArrayList<Pengguna>, private val itemOnClick: ItemOnClick)
    : RecyclerView.Adapter<PenyewaAdapter.ViewHolder>()
{

    class ViewHolder(layoutPenyewa: LayoutPenyewaBinding)
        :RecyclerView.ViewHolder(layoutPenyewa.root)
    {
            private var bind=layoutPenyewa

            fun bind(dataPenyewa:Pengguna, itemOnClick: ItemOnClick)
            {
                itemView.apply {
                    bind.lblpeminjamusername.text=dataPenyewa.username
                    bind.lblpeminjamemail.text=dataPenyewa.email
                }

                bind.btnpenyewadetail.setOnClickListener { view->
                    itemOnClick.OnClickDetail(view, dataPenyewa)
                }
                bind.btnpenyewahapus.setOnClickListener { view->
                    itemOnClick.OnClickHapus(view, dataPenyewa)
                }
            }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind=LayoutPenyewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayListPenyewa[position], itemOnClick)
    }

    override fun getItemCount(): Int {
        return arrayListPenyewa.size
    }

    interface ItemOnClick
    {
        fun OnClickDetail(view: View, pengguna: Pengguna)
        fun OnClickHapus(view: View, pengguna: Pengguna)
    }
}