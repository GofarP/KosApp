package com.example.kosapp.Fragment

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kosapp.Activity.DetailVerifikasiAkunActivity
import com.example.kosapp.Activity.DetailVerifikasiKosActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanVerifikasiAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanVerifikasiAdapter.OnItemClickListener
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.PermintaanVerifikasi
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentVerifikasiBinding
import com.example.kosapp.databinding.LayoutDetailVerifikasiAkunBinding
import com.example.kosapp.databinding.LayoutDetailVerifikasiKosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class VerifikasiFragment : Fragment(), OnItemClickListener {

    private var database= Firebase.database.reference
    private var storage=FirebaseStorage.getInstance().reference
    private var verifikasiArrayList=ArrayList<PermintaanVerifikasi>()
    private var calendar=Calendar.getInstance()

    private lateinit var permintaanVerifikasiAdapter: PermintaanVerifikasiAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var permintaanVerifikasi: PermintaanVerifikasi
    private lateinit var transaksi: Transaksi
    private lateinit var binding:FragmentVerifikasiBinding
    private lateinit var tanggalHariIni:String
    private lateinit var format:Format



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentVerifikasiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        format= SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault())

        getDataVerifikasi()
    }


    private fun getDataVerifikasi()
    {
        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    verifikasiArrayList.clear()
                    binding.rvadminverifikasi.adapter=null

                        snapshot.children.forEach {snap->
                            permintaanVerifikasi= PermintaanVerifikasi(
                                email = snap.child(Constant().KEY_EMAIL).value.toString(),
                                id = snap.child(Constant().KEY_ID_PENGGUNA).value.toString(),
                                idPermintaan = snap.child(Constant().KEY_ID_PERMINTAAN).value.toString(),
                                isi=snap.child(Constant().KEY_ISI).value.toString(),
                                judul = snap.child(Constant().KEY_JUDUL).value.toString(),
                                tanggal = snap.child(Constant().KEY_TANGGAL).value.toString(),
                                username=snap.child(Constant().KEY_USERNAME).value.toString()
                            )

                            verifikasiArrayList.add(permintaanVerifikasi)
                        }

                    permintaanVerifikasiAdapter= PermintaanVerifikasiAdapter(verifikasiArrayList,this@VerifikasiFragment)
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvadminverifikasi.layoutManager=layoutManager
                    binding.rvadminverifikasi.adapter=permintaanVerifikasiAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }
            })
    }


    private fun terimaVerifikasiAkun(permintaanVerifikasi: PermintaanVerifikasi)
    {

        transaksi= Transaksi(
                transaksiId = UUID.randomUUID().toString(),
                dari=Constant().KEY_ROLE_ADMIN,
                isi="Permintaan Verifikasi Diterima",
                judul="Verifikasi Akun",
                kepada = permintaanVerifikasi.email,
                tanggal = tanggalHariIni,
                tipe = Constant().KEY_VERIFIKASI
            )

        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .removeValue()

        database.child(Constant().KEY_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .child(Constant().KEY_STATUS_VERIFIKASI_AKUN)
            .setValue(Constant().KEY_TERVERIFIKASI)

        database.child(Constant().KEY_TRANSAKSI)
            .push()
            .setValue(transaksi)

        Toast.makeText(activity, "Sukses menerima Verifikasi Akun", Toast.LENGTH_SHORT).show()
    }


    private fun tolakVerifikasiAkun(permintaanVerifikasi:PermintaanVerifikasi)
    {
        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasi.idPermintaan)
            .removeValue()

        storage.child(Constant().KEY_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .delete()

        database.child(Constant().KEY_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .child(Constant().KEY_STATUS_VERIFIKASI_AKUN)
            .setValue(Constant().KEY_BELUM_VERIFIKASI)

        val indexVerifikasi=verifikasiArrayList.indexOf(permintaanVerifikasi)
        permintaanVerifikasiAdapter.notifyItemRemoved(indexVerifikasi)

        Toast.makeText(activity, "Sukses Menolak Verifikasi Akun", Toast.LENGTH_SHORT).show()

    }


    private fun detailVerifikasiAkun(permintaanVerifikasi: PermintaanVerifikasi)
    {
        val intent= Intent(activity, DetailVerifikasiAkunActivity::class.java)
            .putExtra(Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN, permintaanVerifikasi)
        startActivity(intent)
    }

    private fun detailVerifikasiKos(permintaanVerifikasi: PermintaanVerifikasi)
    {
        val intent=Intent(activity, DetailVerifikasiKosActivity::class.java)
            .putExtra(Constant().KEY_ID_KOS, permintaanVerifikasi.id)
        startActivity(intent)
    }

    private fun terimaVerifikasiKos(permintaanVerifikasi: PermintaanVerifikasi)
    {
        transaksi= Transaksi(
            transaksiId = UUID.randomUUID().toString(),
            dari=Constant().KEY_ROLE_ADMIN,
            isi="Permintaan Verifikasi Diterima",
            judul="Verifikasi Kos",
            kepada = permintaanVerifikasi.email,
            tanggal = tanggalHariIni,
            tipe = Constant().KEY_VERIFIKASI
        )

        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasi.id)
            .removeValue()

        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasi.id)
            .child(Constant().KEY_STATUS_VERIFIKASI_AKUN)
            .setValue(Constant().KEY_TERVERIFIKASI)

        database.child(Constant().KEY_TRANSAKSI)
            .push()
            .setValue(transaksi)

        Toast.makeText(activity, "Sukses menerima Verifikasi Kos", Toast.LENGTH_SHORT).show()
    }

    private fun tolakVerifikasiKos(permintaanVerifikasi: PermintaanVerifikasi)
    {
        database.child(Constant().KEY_PERMINTAAN_VERIFIKASI)
            .child(permintaanVerifikasi.idPermintaan)
            .removeValue()


        database.child(Constant().KEY_DAFTAR_KOS)
            .child(permintaanVerifikasi.id)
            .removeValue()

        storage.child(Constant().KEY_GAMBAR_KOS)
            .child(permintaanVerifikasi.id)
            .delete()

        val indexVerifikasi=verifikasiArrayList.indexOf(permintaanVerifikasi)
        permintaanVerifikasiAdapter.notifyItemRemoved(indexVerifikasi)

        Toast.makeText(activity, "Sukses Menolak Verifikasi Akun", Toast.LENGTH_SHORT).show()

    }


    override fun onDetailClickListener(view: View, permintaanVerifikasi: PermintaanVerifikasi) {

        if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN)
        {
            detailVerifikasiAkun(permintaanVerifikasi)
        }

        else if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_KOS)
        {
            detailVerifikasiKos(permintaanVerifikasi)
        }
    }

    override fun onTerimaClickListener(view: View, permintaanVerifikasi: PermintaanVerifikasi) {
        if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN)
        {
            terimaVerifikasiAkun(permintaanVerifikasi)
        }

        else if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_KOS)
        {
            terimaVerifikasiKos(permintaanVerifikasi)
        }
    }

    override fun onTolakClickListener(view: View, permintaanVerifikasi: PermintaanVerifikasi) {
        if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_AKUN)
        {
            tolakVerifikasiAkun(permintaanVerifikasi)
        }

        else if(permintaanVerifikasi.judul==Constant().KEY_PERMINTAAN_VERIFIKASI_KOS)
        {
            tolakVerifikasiKos(permintaanVerifikasi)
        }
    }
}