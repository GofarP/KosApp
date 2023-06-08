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
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.RatingProfileActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PermintaanAdapter.OnClickListener
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.Model.Permintaan
import com.example.kosapp.Model.Sewa
import com.example.kosapp.databinding.FragmentPermintaanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PermintaanFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentPermintaanBinding
    private lateinit var adapter:PermintaanAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var database=Firebase.database.reference
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var permintaanArrayList=ArrayList<Permintaan>()
    private lateinit var permintaan: Permintaan
    private lateinit var sewa: Sewa
    private lateinit var transaksi: Transaksi
    private lateinit var history: History
    private lateinit var format:SimpleDateFormat
    private var calendar=Calendar.getInstance()
    private lateinit var tanggalHariIni:String
    private lateinit var intent:Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPermintaanBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tanggalHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        format= SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault())

        getPermintaan()

    }


    private fun getPermintaan()
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    permintaanArrayList.clear()

                    snapshot.children.forEach { snap->

                            val emailChildKepada=snap.child(Constant().KEY_KEPADA).value.toString()
                            val emailChildDari=snap.child(Constant().KEY_DARI).value.toString()

                            if(emailChildDari == emailPengguna || emailChildKepada==emailPengguna)
                            {
                                permintaan=Permintaan(
                                    dari=snap.child(Constant().KEY_DARI).value.toString(),
                                    idKos=snap.child(Constant().KEY_ID_KOS).value.toString(),
                                    idPermintaan = snap.child(Constant().KEY_ID_PERMINTAAN).value.toString(),
                                    idPengguna=snap.child(Constant().KEY_ID_PENGGUNA).value.toString(),
                                    isi=snap.child(Constant().KEY_ISI).value.toString(),
                                    judul=snap.child(Constant().KEY_JUDUL).value.toString(),
                                    kepada = snap.child(Constant().KEY_KEPADA).value.toString(),
                                    namaKos = snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                                    tanggal =  tanggalHariIni,
                                )
                                permintaanArrayList.add(permintaan)
                            }

                            adapter= PermintaanAdapter(permintaanArrayList, this@PermintaanFragment)
                            layoutManager=LinearLayoutManager(activity)
                            binding.rvpermintaan.layoutManager=layoutManager
                            binding.rvpermintaan.adapter=adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }


    private fun terimaPermintaanSewaKos(dataPermintaan: Permintaan)
    {

        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==dataPermintaan.idPermintaan)
                        {
                            snap.ref.removeValue()
                                .addOnSuccessListener {

                                    sewa=Sewa(
                                        idSewa = UUID.randomUUID().toString(),
                                        email=dataPermintaan.dari,
                                        tanggal=tanggalHariIni,
                                        idKos = dataPermintaan.idKos
                                    )

                                    transaksi=Transaksi(
                                        dari=emailPengguna,
                                        transaksiId = UUID.randomUUID().toString(),
                                        isi="Permintaan Sewa Diterima",
                                        judul=Constant().KEY_PERMINTAAN_SEWA,
                                        kepada=dataPermintaan.dari,
                                        tanggal=tanggalHariIni,
                                        tipe=Constant().KEY_TERIMA_SEWA
                                    )

                                    database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                                        .push()
                                        .setValue(sewa)

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .push()
                                        .setValue(transaksi)


                                    database.child(Constant().KEY_DAFTAR_KOS)
                                        .child(permintaan.idKos)
                                        .get().addOnSuccessListener { snap->
                                            history= History(
                                                idHistory=UUID.randomUUID().toString(),
                                                idKos = snap.child(Constant().KEY_ID_KOS).value.toString(),
                                                alamat = snap.child(Constant().KEY_ALAMAT_KOS).value.toString(),
                                                nama=snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                                                tanggal = tanggalHariIni,
                                                thumbnailKos = snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()
                                            )

                                            database.child(Constant().KEY_HISTORY)
                                                .child(permintaan.dari.replace(".",","))
                                                .push()
                                                .setValue(history)
                                        }

                                    database.child(Constant().KEY_DAFTAR_KOS)
                                        .child(permintaan.idKos)
                                        .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                        .setValue(ServerValue.increment(-1))


                                    Toast.makeText(activity, "Sukses Menerima Permintaan", Toast.LENGTH_SHORT).show()


                                    val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                    adapter.notifyItemRemoved(indexPermintaan)

                                }
                                .addOnFailureListener {error->
                                    Toast.makeText(activity, "Gagal Menerima Permintaan", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db",error.message)
                }

            })
    }

    private fun terimaPermintaanKeluarKos(permintaanId: String)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {snap->
                        val permintaanIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val permintaanDari=snap.child(Constant().KEY_DARI).value.toString()

                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==permintaanId)
                        {
                            snap.ref.removeValue()

                            transaksi=Transaksi(
                                dari=emailPengguna,
                                transaksiId = UUID.randomUUID().toString(),
                                isi="Permintaan Keluar Kos ${permintaan.namaKos} Diterima",
                                judul=Constant().PERMINTAAAN_AKHIRI_SEWA,
                                kepada=permintaan.dari,
                                tanggal=tanggalHariIni,
                                tipe=Constant().KEY_AKHIRI_SEWA
                            )

                            transaksi=Transaksi(
                                dari=emailPengguna,
                                transaksiId = UUID.randomUUID().toString(),
                                isi="Permintaan Keluar Kos Diterima",
                                judul=Constant().PERMINTAAAN_AKHIRI_SEWA,
                                kepada=permintaan.dari,
                                tanggal=tanggalHariIni,
                                tipe=Constant().KEY_AKHIRI_SEWA
                            )

                            database.child(Constant().KEY_TRANSAKSI)
                                .push()
                                .setValue(transaksi)

                            database.child(Constant().KEY_DAFTAR_KOS)
                                .child(permintaan.idKos)
                                .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                .setValue(ServerValue.increment(1))

                            database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                                .addValueEventListener(object:ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach {snap->
                                            val emailSewa=snap.child(Constant().KEY_EMAIL).value.toString()
                                            val idKosSewa=snap.child(Constant().KEY_ID_KOS).value.toString()

                                            if(permintaanDari==emailSewa && permintaanIdKos==idKosSewa)
                                            {
                                                snap.ref.removeValue()
                                                    .addOnSuccessListener {
                                                        Toast.makeText(activity, "Sukses Keluar", Toast.LENGTH_SHORT)
                                                            .show()
                                                        val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                                        adapter.notifyItemRemoved(indexPermintaan)
                                                    }

                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("error",error.message)
                                    }

                                } )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db",error.message)
                }

            })
    }

    private fun tolakPermintaan(permintaanId:String)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==permintaanId)
                        {
                           snap.ref.removeValue()
                               .addOnSuccessListener {

                                   val transaksi=Transaksi(
                                       transaksiId=UUID.randomUUID().toString(),
                                       judul = "Permintaan Sewa Ditolak",
                                       isi = "Anda Menolak Permintaan Sewa",
                                       tipe=Constant().KEY_TOLAK_SEWA,
                                       dari=emailPengguna,
                                       kepada=permintaan.dari,
                                       tanggal=tanggalHariIni
                                   )
                                   database.child(Constant().KEY_TRANSAKSI)
                                       .push()
                                       .setValue(transaksi)

                                   Toast.makeText(activity, "Sukses Menolak Permintaan", Toast.LENGTH_SHORT).show()

                                   val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                   adapter.notifyItemRemoved(indexPermintaan)

                               }
                               .addOnFailureListener {
                                   Toast.makeText(activity, "Gagal Menolak Permintaan", Toast.LENGTH_SHORT).show()
                               }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database Error",error.message)
                }

            })
    }



    private fun batalkanPermintaan(idPermintaan: String)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach { snap->
                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==idPermintaan)
                        {
                            snap.ref.removeValue()
                                .addOnSuccessListener {

                                    val transaksi=Transaksi(
                                        transaksiId=UUID.randomUUID().toString(),
                                        judul = "Permintaan Sewa Dibatalkan",
                                        isi = "Anda Membatalkan Permintaan Sewa Kos ${permintaan.namaKos}",
                                        tipe=Constant().KEY_BATAL_SEWA,
                                        dari=emailPengguna,
                                        kepada=permintaan.dari,
                                        tanggal=tanggalHariIni
                                    )

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .push()
                                        .setValue(transaksi)

                                    Toast.makeText(activity, "Sukses Membatalkan Permintaan", Toast.LENGTH_SHORT).show()

                                    val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                    adapter.notifyItemRemoved(indexPermintaan)

                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   Log.d("snap",error.message)
                }

            })
    }


    private fun lihatProfilePengguna(idPengguna:String)
    {
        intent= Intent(requireActivity(),RatingProfileActivity::class.java)
        intent.putExtra("idPengguna",idPengguna)
        startActivity(intent)
    }

    override fun onTerimaCLickListener(view: View, dataPermintaan: Permintaan) {
        if(dataPermintaan.judul==Constant().PERMINTAAAN_AKHIRI_SEWA)
        {
            terimaPermintaanKeluarKos(dataPermintaan.idPermintaan)
        }

        else if(dataPermintaan.judul==Constant().KEY_PERMINTAAN_SEWA)
        {
            terimaPermintaanSewaKos(dataPermintaan)
        }
    }

    override fun onTolakClickListener(view: View, dataPermintaan: Permintaan) {
        if(dataPermintaan.dari==emailPengguna)
        {
            batalkanPermintaan(dataPermintaan.idPermintaan)
        }

        else
        {
            tolakPermintaan(dataPermintaan.idPermintaan)
        }
    }

    override fun onLihatProfileListener(view: View, dataPermintaan: Permintaan) {
        lihatProfilePengguna(dataPermintaan.idPengguna)
    }


}