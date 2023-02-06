package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.LayoutKosMenyewaBinding
import com.google.firebase.storage.FirebaseStorage

class MenyewaAdapter(val kosArrayList: ArrayList<Kos>, private val itemOnClickMenyewakan: ItemOnCLickMenyewa)
    :RecyclerView.Adapter<MenyewaAdapter.ViewHolderDisewa>() {


        class ViewHolderDisewa(layoutMenyewaBinding: LayoutKosMenyewaBinding)
            :RecyclerView.ViewHolder(layoutMenyewaBinding.root) {
                private val binding=layoutMenyewaBinding
                private val storage=FirebaseStorage.getInstance().reference

                 fun bind(dataKos:Kos, itemAdapterCallback:ItemOnCLickMenyewa)
                 {
                     itemView.apply {
                         binding.lblnama.text=dataKos.nama
                         binding.lblalamat.text=dataKos.alamat

                         storage.child(dataKos.thumbnailKos)
                             .downloadUrl.addOnSuccessListener { url->
                             Glide.with(this.context)
                                 .load(url)
                                 .into(binding.ivkos)
                         }.addOnFailureListener {
                             Log.d("Failed", it.message.toString())
                         }

                         binding.btnselengkapnya.setOnClickListener {view->
                             itemAdapterCallback.OnSelengkapnyaClick(view, dataKos)
                         }

                         binding.btnpetunjuk.setOnClickListener {view->
                            itemAdapterCallback.OnPetunjukClick(view, dataKos)
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
        fun OnPetunjukClick(v:View, dataKos: Kos)
    }


}