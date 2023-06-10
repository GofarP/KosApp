package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.BuktiTransfer
import com.example.kosapp.R
import com.example.kosapp.databinding.LayoutMenuBuktiTransferBinding
import com.google.firebase.auth.FirebaseAuth

class BuktiTransferAdapter(private val arrayListBuktiTransfer:ArrayList<BuktiTransfer>,
private val itemClickLihatBukti:ItemOnCLickLihatBukti):
    RecyclerView.Adapter<BuktiTransferAdapter.ViewHolder>() {

    class ViewHolder(layoutMenuBuktiTransferBinding: LayoutMenuBuktiTransferBinding)
        :RecyclerView.ViewHolder(layoutMenuBuktiTransferBinding.root)
    {
        private val binding=layoutMenuBuktiTransferBinding

        private val emailSaatIni=FirebaseAuth.getInstance().currentUser?.email.toString()

        fun bind(dataBuktiTransfer: BuktiTransfer, itemClickLihatBukti: ItemOnCLickLihatBukti)
        {

            itemView.apply {
                binding.lbljudulbuktitransfer.text="Bukti Transfer ${dataBuktiTransfer.namaKos}"
                binding.lblnamapengirim.text="Pengirim: ${dataBuktiTransfer.emailPenyewa}"
                binding.lbltanggal.text= dataBuktiTransfer.tanggal

                if(dataBuktiTransfer.urlBuktiTransfer=="" && dataBuktiTransfer.emailPenyewa==emailSaatIni)
                {
                    binding.btnlihatbuktitransfer.text="Upload Bukti Transfer"
                }

                else if(dataBuktiTransfer.urlBuktiTransfer!="" && dataBuktiTransfer.emailPenyewa==emailSaatIni)
                {
                    binding.btnlihatbuktitransfer.text="Lihat Bukti Transfer"
                }

                else if(dataBuktiTransfer.urlBuktiTransfer=="" && dataBuktiTransfer.emailPemilik==emailSaatIni)
                {
                    binding.btnlihatbuktitransfer.text="Bukti Transfer Belum Diupload"
                    binding.btnlihatbuktitransfer.isEnabled=false
                    binding.btnlihatbuktitransfer.setBackgroundResource(R.drawable.button_background_disabled)

                }

                else if(dataBuktiTransfer.urlBuktiTransfer!="" && dataBuktiTransfer.emailPemilik==emailSaatIni)
                {
                    binding.btnlihatbuktitransfer.text="Lihat Bukti Transfer"
                    binding.btnlihatbuktitransfer
                    binding.btnlihatbuktitransfer.isEnabled=true
                }

            }

            binding.btnlihatbuktitransfer.setOnClickListener {view->
                itemClickLihatBukti.OnClick(view, dataBuktiTransfer)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding=LayoutMenuBuktiTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayListBuktiTransfer[position], itemClickLihatBukti)
    }

    override fun getItemCount(): Int {
            return arrayListBuktiTransfer.size
    }


}

interface ItemOnCLickLihatBukti
{
    fun OnClick(v: View, dataBuktiTransfer: BuktiTransfer)
}