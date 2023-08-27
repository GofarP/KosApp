package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Activity.DetailVerifikasiKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanVerifikasiAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanVerifikasiKosAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.Model.PermintaanVerifikasiKos
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentVerifikasiBinding
import com.example.kosapp.databinding.FragmentVerifikasiKosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VerifikasiKosFragment : Fragment(), PermintaanVerifikasiKosAdapter.OnItemClickListener {

    private lateinit var binding: FragmentVerifikasiKosBinding
    private lateinit var tanggalHariIni:String
    private lateinit var format: Format
    private lateinit var permintaanVerifikasiKos:PermintaanVerifikasiKos
    private lateinit var adapter:PermintaanVerifikasiKosAdapter
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var transaksi:Transaksi

    private var calendar=Calendar.getInstance()
    private var database=FirebaseDatabase.getInstance().reference
    private var arrayListVerifikasiKos=ArrayList<PermintaanVerifikasiKos>()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var storage=FirebaseStorage.getInstance().reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentVerifikasiKosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        format= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

        getDataVerifikasi()

    }


    private fun getDataVerifikasi()
    {
        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListVerifikasiKos.clear()
                    binding.rvverifikasikos .adapter=null

                    snapshot.children.forEach {snap->

                        val snapJudul=snap.child(Constant().KEY_JUDUL).value.toString()
                        if(snapJudul==Constant().KEY_PERMINTAAN_VERIFIKASI_KOS)
                        {
                            permintaanVerifikasiKos= PermintaanVerifikasiKos(
                                email = snap.child(Constant().KEY_EMAIL).value.toString(),
                                idKos=snap.child(Constant().KEY_ID_KOS).value.toString(),
                                idPermintaan = snap.child(Constant().KEY_ID_PERMINTAAN).value.toString(),
                                idPemohon=snap.child(Constant().KEY_ID_PEMOHON).value.toString(),
                                isi=snap.child(Constant().KEY_ISI).value.toString(),
                                judul = snap.child(Constant().KEY_JUDUL).value.toString(),
                                tanggal = snap.child(Constant().KEY_TANGGAL).value.toString(),
                                username=snap.child(Constant().KEY_USERNAME).value.toString()
                            )

                            arrayListVerifikasiKos.add(permintaanVerifikasiKos)

                        }


                    }

                    adapter= PermintaanVerifikasiKosAdapter(arrayListVerifikasiKos,this@VerifikasiKosFragment)
                    layoutManager= LinearLayoutManager(activity)
                    binding.rvverifikasikos.layoutManager=layoutManager
                    binding.rvverifikasikos.adapter=adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }
            })
    }


    private fun detailVerifikasiKos(permintaanVerifikasiKos: PermintaanVerifikasiKos)
    {
        val intent= Intent(activity, DetailVerifikasiKosActivity::class.java)
            .putExtra(Constant().KEY_ID_KOS, permintaanVerifikasiKos.idKos)
        startActivity(intent)
    }


    override fun onDetailClickListener(
        view: View,
        permintaanVerifikasiKos: PermintaanVerifikasiKos
    ) {
        detailVerifikasiKos(permintaanVerifikasiKos)
    }

    private fun terimaVerifikasiKos(permintaanVerifikasiKos: PermintaanVerifikasiKos)
    {
        transaksi= Transaksi(
            idTransaksi = UUID.randomUUID().toString(),
            idPemilik = idPengguna,
            idPenyewa = permintaanVerifikasiKos.idPemohon,
            isi="Permintaan Verifikasi Diterima",
            judul="Verifikasi Kos",
            tanggal = tanggalHariIni,
            tipe = Constant().KEY_VERIFIKASI
        )

        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasiKos.idKos)
            .removeValue()

        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasiKos.idKos)
            .child(Constant().KEY_STATUS_VERIFIKASI_AKUN)
            .setValue(Constant().KEY_BIAYA_BUKA_KOS)

        database.child(Constant().KEY_TRANSAKSI)
            .child(permintaanVerifikasiKos.idPemohon)
            .push()
            .setValue(transaksi)

        transaksi.isi="Anda Menerima Permintaan Verifikasi Kos Dari ${permintaanVerifikasiKos.email}"

        database.child(Constant().KEY_TRANSAKSI)
            .child(idPengguna)
            .push()
            .setValue(transaksi)


        Toast.makeText(activity, "Sukses menerima Verifikasi Kos", Toast.LENGTH_SHORT).show()
    }

    private fun tolakVerifikasiKos(permintaanVerifikasiKos: PermintaanVerifikasiKos)
    {

        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasiKos.idKos)
            .get().addOnSuccessListener {snap->
                val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()
                val snapGambarKos=snap.child(Constant().KEY_GAMBAR_KOS).value.toString()

                storage.child(snapThumbnailKos).delete()

                storage.child(snapGambarKos).delete()
            }

        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasiKos.idKos)
            .removeValue()

        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasiKos.idKos)
            .removeValue()

        val indexVerifikasi=arrayListVerifikasiKos.indexOf(permintaanVerifikasiKos)
        adapter.notifyItemRemoved(indexVerifikasi)

        Toast.makeText(activity, "Sukses Menolak Verifikasi Akun", Toast.LENGTH_SHORT).show()

    }

    override fun onTerimaClickListener(
        view: View,
        permintaanVerifikasiKos: PermintaanVerifikasiKos
    ) {
        terimaVerifikasiKos(permintaanVerifikasiKos)
    }

    override fun onTolakClickListener(
        view: View,
        permintaanVerifikasiKos: PermintaanVerifikasiKos
    ) {
        tolakVerifikasiKos(permintaanVerifikasiKos)
    }


}