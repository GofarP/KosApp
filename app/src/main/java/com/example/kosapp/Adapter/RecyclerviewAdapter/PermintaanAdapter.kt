package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.databinding.LayoutPermintaanBinding
import com.google.firebase.auth.FirebaseAuth

class PermintaanAdapter(private var permintaanArrayList:ArrayList<Permintaan>,private val itemClickListner:OnClickListener)
    :RecyclerView.Adapter<PermintaanAdapter.ViewHolder>() {

        class ViewHolder(layoutPermintaanBinding: LayoutPermintaanBinding)
            :RecyclerView.ViewHolder(layoutPermintaanBinding.root)
        {
                 val binding=layoutPermintaanBinding
                 val emailUser=FirebaseAuth.getInstance().currentUser?.email.toString()
                 var isiPermintaan:String?=null
                 var judulPermintaan:String?=null

                 fun bind(dataPermintaan:Permintaan, itemClickListner: OnClickListener)
                 {
                     judulPermintaan=dataPermintaan.judul

                     itemView.apply {

                         if(emailUser==dataPermintaan.kepada)
                         {
                            isiPermintaan="${dataPermintaan.dari} ${dataPermintaan.isi}"
                         }

                         else
                         {
                             isiPermintaan="Anda ${dataPermintaan.isi}"
                             binding.btntolak.text="Batalkan"
                             binding.btnterima.visibility=View.GONE
                         }

                         binding.lbljudulpermintaan.text=judulPermintaan
                         binding.lblisipermintaan.text=isiPermintaan
                         binding.lbltanggal.text= dataPermintaan.tanggal.toString()

                         binding.btnterima.setOnClickListener {view->
                             itemClickListner.onTerimaCLickListener(view, dataPermintaan)
                         }

                         binding.btntolak.setOnClickListener { view->
                             itemClickListner.onTolakClickListener(view, dataPermintaan)
                         }
                     }

                 }

            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind=LayoutPermintaanBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(permintaanArrayList[position], itemClickListner)
    }

    override fun getItemCount(): Int {
        return permintaanArrayList.size
    }

    interface OnClickListener
    {
        fun onTerimaCLickListener(view: View, dataPermintaan: Permintaan)
        fun onTolakClickListener(view: View, dataPermintaan: Permintaan)
    }
}