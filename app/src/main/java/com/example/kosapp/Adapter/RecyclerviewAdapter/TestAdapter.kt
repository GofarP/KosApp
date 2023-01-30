package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutKosMenyewaBinding

@Suppress("SpellCheckingInspection")
class NegaraAdapter(private val kosArrayList: List<Kos>)
    : RecyclerView.Adapter<NegaraAdapter.NegaraViewHolder>() {

    var listener: RecyclerViewClickListener? = null

    interface RecyclerViewClickListener {
        fun onItemClicked(view: View, kos: Kos)
    }

    inner class NegaraViewHolder(val layoutKosMenyewaBinding: LayoutKosMenyewaBinding)
        : RecyclerView.ViewHolder(layoutKosMenyewaBinding.root)

    // untuk mendapatkan jumlah data yang dimasukkan ke dalam adapter
    override fun getItemCount() = kosArrayList.size

    // untuk membuat setiap item recyclerview berdasarkan jumlah data yang dimasukkan ke dalam adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegaraViewHolder {
        val binding=LayoutKosMenyewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return NegaraViewHolder(binding)
    }

    // untuk memasukkan atau set data ke dalam view
    override fun onBindViewHolder(holder: NegaraViewHolder, position: Int) {
        holder.layoutKosMenyewaBinding.lblnama.text = kosArrayList[position].nama
        holder.layoutKosMenyewaBinding.lblalamat.text = kosArrayList[position].alamat

        // event onclick pada setiap item
        holder.layoutKosMenyewaBinding.btnselengkapnya.setOnClickListener {
            listener?.onItemClicked(it, kosArrayList[position])
        }
    }

}