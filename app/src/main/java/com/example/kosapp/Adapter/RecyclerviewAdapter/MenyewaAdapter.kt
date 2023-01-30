package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutKosMenyewaBinding

class MenyewaAdapter(val kosArrayList: ArrayList<Kos>, private val itemOnClickMenyewakan: ItemOnCLickMenyewa)
    :RecyclerView.Adapter<MenyewaAdapter.ViewHolderDisewa>() {



        class ViewHolderDisewa(layoutMenyewaBinding: LayoutKosMenyewaBinding)
            :RecyclerView.ViewHolder(layoutMenyewaBinding.root) {
                private val binding=layoutMenyewaBinding

                 fun bind(dataKos:Kos, itemAdapterCallback:ItemOnCLickMenyewa)
                 {
                     itemView.apply {
                         binding.lblnama.text=dataKos.nama
                         binding.lblalamat.text=dataKos.alamat


                         binding.btnselengkapnya.setOnClickListener {view->
                             itemAdapterCallback.OnSelengkapnyaClick(view, dataKos)
                         }

                     }
                 }

            }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDisewa {
        val binding=LayoutKosMenyewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolderDisewa(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderDisewa, position: Int) {
        holder.bind(kosArrayList[position], itemOnClickMenyewakan)
    }

    override fun getItemCount(): Int {
        return kosArrayList.size
    }


    interface ItemOnCLickMenyewa
    {
        fun OnSelengkapnyaClick(v: View, dataKos: Kos)
    }


}