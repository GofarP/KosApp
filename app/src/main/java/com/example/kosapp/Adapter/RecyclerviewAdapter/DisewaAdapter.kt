package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutKosDisewaBinding
import com.google.firebase.storage.FirebaseStorage

class DisewaAdapter(val kosArrayList: ArrayList<Kos>, private val itemOnClickDisewakan: ItemOnClickDisewa)
    :RecyclerView.Adapter<DisewaAdapter.ViewHolderDisewa>() {

        private val TYPE_DISEWA=1
        private val TYPE_MENYEWA=2

        class ViewHolderDisewa(layoutDisewaBinding: LayoutKosDisewaBinding)
            :RecyclerView.ViewHolder(layoutDisewaBinding.root) {
                private val binding=layoutDisewaBinding
                private val storage=FirebaseStorage.getInstance().reference

                 fun bind(dataKos:Kos, itemAdapterCallback:ItemOnClickDisewa)
                 {
                     itemView.apply {
                         binding.lblnama.text=dataKos.namaKos
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

                         storage.child(dataKos.thumbnailKos)
                             .downloadUrl.addOnSuccessListener { uri->
                                 Glide.with(this.context)
                                     .load(uri)
                                     .into(binding.ivkos)
                             }

                     }
                 }

            }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDisewa {
        val binding=LayoutKosDisewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolderDisewa(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderDisewa, position: Int) {
        holder.bind(kosArrayList[position], itemOnClickDisewakan)
    }

    override fun getItemCount(): Int {
        return kosArrayList.size
    }

    interface  ItemOnClickDisewa
    {
        fun OnEditClick(v: View, dataKos: Kos)

        fun OnDeleteClick(v:View, dataKos: Kos)

        fun onPeminjamClick(v:View, dataKos: Kos)
    }

    interface ItemOnCLickMenyewa
    {
        fun OnSelengkapnyaClick(v: View, dataKos: Kos)
    }

}