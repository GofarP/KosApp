package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.databinding.LayoutTransaksiBinding
import com.google.firebase.auth.FirebaseAuth

class TransaksiAdapter(private val listTransaksi: ArrayList<Transaksi>, )
    :RecyclerView.Adapter<TransaksiAdapter.ViewHolder>()
{
        class ViewHolder(layoutTransaksi: LayoutTransaksiBinding)
            :RecyclerView.ViewHolder(layoutTransaksi.root)
        {
                val binding=layoutTransaksi
                var judulTransaksi:String?=null
                var isiTransaksi:String?=null
                val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()

                fun bind(dataTransaksi: Transaksi)
                {

                    judulTransaksi=dataTransaksi.judul

                    if(dataTransaksi.dari==emailPengguna && dataTransaksi.tipe==Constant().KEY_TOLAK_SEWA)
                    {
                       isiTransaksi="Anda Menolak Permintaan Kos dari ${dataTransaksi.kepada}"
                    }
                    else if(dataTransaksi.dari==emailPengguna && dataTransaksi.tipe==Constant().KEY_BATAL_SEWA)
                    {
                        isiTransaksi="Anda Membatalkan Permintaan Kos dari ${dataTransaksi.kepada} "
                    }

                    else if(dataTransaksi.dari==emailPengguna && dataTransaksi.tipe==Constant().KEY_TERIMA_SEWA)
                    {
                        isiTransaksi="Anda Menerima Permintaan Sewa Kos dari ${dataTransaksi.kepada}"
                    }

                    else
                    {
                        isiTransaksi="${dataTransaksi.isi} oleh ${dataTransaksi.dari}"
                    }

                    binding.lbljudulhistory.text=judulTransaksi
                    binding.lblbodyhistory.text=isiTransaksi
                    binding.lbltglhistory.text=dataTransaksi.tanggal

                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding=LayoutTransaksiBinding.inflate(LayoutInflater.from(parent.context),parent, false )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.bind(listTransaksi[position])
        }

        override fun getItemCount(): Int {
            return listTransaksi.size
        }

}