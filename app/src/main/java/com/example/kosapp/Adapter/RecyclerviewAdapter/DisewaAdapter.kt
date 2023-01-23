package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentDisewaBinding
import com.example.kosapp.databinding.LayoutKosDisewaBinding

class DisewaAdapter(val kosArrayList: ArrayList<Kos>, private val itemOnClick: ItemOnClick)
    :RecyclerView.Adapter<DisewaAdapter.ViewHolder>() {

        class ViewHolder(layoutDisewaBinding: LayoutKosDisewaBinding)
            :RecyclerView.ViewHolder(layoutDisewaBinding.root) {
                private val binding=layoutDisewaBinding

                 fun bind(dataKos:Kos, itemAdapterCallback:ItemOnClick)
                 {
                     itemView.apply {
                         binding.lblnama.text=dataKos.nama
                         binding.lblalamat.text=dataKos.alamat

                         binding.btndisewaedit.setOnClickListener {view->
                             itemAdapterCallback.OnEditClick(view, dataKos)
                         }

                         binding.btndisewahapus.setOnClickListener { view->
                             itemAdapterCallback.OnDeleteClick(view, dataKos)
                         }

                         binding.btndisewapeminjam.setOnClickListener {view->
                             itemAdapterCallback.onPeminjamClick(view, dataKos)
                         }

                     }
                 }

            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutKosDisewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(kosArrayList[position], itemOnClick)
    }

    override fun getItemCount(): Int {
        return kosArrayList.size
    }

    interface  ItemOnClick
    {
        fun OnEditClick(v: View, dataKos: Kos)

        fun OnDeleteClick(v:View, dataKos: Kos)

        fun onPeminjamClick(v:View, dataKos: Kos)
    }
}