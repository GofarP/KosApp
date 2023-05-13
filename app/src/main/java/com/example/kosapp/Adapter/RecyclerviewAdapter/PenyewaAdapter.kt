package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.databinding.LayoutPenyewaAdminBinding
import com.example.kosapp.databinding.LayoutPenyewaBinding
import com.google.firebase.storage.FirebaseStorage

class PenyewaAdapter(private val arrayListPenyewa:ArrayList<Pengguna>, val viewType: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var adminClickListener: AdminItemOnClick?=null
    var penggunaClickListener: PenggunaItemOnCLick?=null

    val VIEW_ADMIN=1
    val VIEW_PENGGUNA=2

    class PenggunaViewHolder(layoutPenyewa: LayoutPenyewaBinding)
        :RecyclerView.ViewHolder(layoutPenyewa.root)
    {
            val bind=layoutPenyewa

            val storage=FirebaseStorage.getInstance()

            fun bind(dataPengguna:Pengguna)
            {
                itemView.apply {
                    bind.lblnamapenyewa.text=dataPengguna.username
                    bind.lblemailpenyewa.text=dataPengguna.email

                    storage.reference.child(dataPengguna.foto)
                        .downloadUrl.addOnSuccessListener { uri->
                            Glide.with(this)
                                .load(uri)
                                .into(bind.ivpenyewa)
                        }
                }

            }

    }

    class AdminViewHolder(layoutPenyewaAdminBinding: LayoutPenyewaAdminBinding)
        :RecyclerView.ViewHolder(layoutPenyewaAdminBinding.root)
    {
        val bind=layoutPenyewaAdminBinding

        val storage=FirebaseStorage.getInstance()

        fun binding(dataPengguna:Pengguna)
        {
            itemView.apply {
                bind.lblnamapenyewa.text=dataPengguna.username
                bind.lblemailpenyewa.text=dataPengguna.email

                storage.reference.child(dataPengguna.foto)
                    .downloadUrl.addOnSuccessListener { uri->
                        Glide.with(this)
                            .load(uri)
                            .into(bind.ivpenyewa)
                    }
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType)
        {
            VIEW_ADMIN->{
                val bind=LayoutPenyewaAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                AdminViewHolder(bind)
            }
            else->{
                val bind=LayoutPenyewaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                PenggunaViewHolder(bind)
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position))
        {
            VIEW_ADMIN->{
                (holder as AdminViewHolder).binding(arrayListPenyewa[position])

                holder.bind.btnpenyewadetail.setOnClickListener {view->
                    adminClickListener?.OnClickDetail(view, arrayListPenyewa[position])
                }

            }

            VIEW_PENGGUNA->{
                (holder as PenggunaViewHolder).bind(arrayListPenyewa[position])

                holder.bind.btnpenyewadetail.setOnClickListener {view->
                    penggunaClickListener?.OnClickDetail(view, arrayListPenyewa[position])
                }

                holder.bind.btnpenyewahapus.setOnClickListener { view->
                    penggunaClickListener?.OnClickDelete(view, arrayListPenyewa[position])
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemCount(): Int {
        return arrayListPenyewa.size
    }

    interface PenggunaItemOnCLick
    {
        fun OnClickDetail(view: View, pengguna: Pengguna)
        fun OnClickDelete(view:View, pengguna:Pengguna)
    }


    interface AdminItemOnClick
    {
        fun OnClickDetail(view:View, pengguna: Pengguna)
    }

}