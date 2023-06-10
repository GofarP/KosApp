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
import com.example.kosapp.Model.*
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
    private lateinit var buktiTransfer:BuktiTransfer
    private lateinit var permintaan: Permintaan
    private lateinit var sewa: Sewa
    private lateinit var transaksi: Transaksi
    private lateinit var history: History
    private lateinit var format:SimpleDateFormat
    private var calendar=Calendar.getInstance()
    private lateinit var tanggalHariIni:String
    private lateinit var intent:Intent
    private lateinit var idTransfer: String
    private var database=Firebase.database.reference
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var permintaanArrayList=ArrayList<Permintaan>()

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

        getPermintaan(view)

    }


    private fun getPermintaan(view:View)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    permintaanArrayList.clear()
                    binding.rvpermintaan.adapter=null

                    snapshot.children.forEach { snap->

                            val idChildPenyewa=snap.child(Constant().KEY_ID_PENYEWA).value.toString()
                            val idChildPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString()

                            if(idChildPemilik == idPengguna || idChildPenyewa==idPengguna)
                            {
                                permintaan=Permintaan(
                                    idKos=snap.child(Constant().KEY_ID_KOS).value.toString(),
                                    idPermintaan = snap.child(Constant().KEY_ID_PERMINTAAN).value.toString(),
                                    idPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString(),
                                    idPenyewa=snap.child(Constant().KEY_ID_PENYEWA).value.toString(),
                                    emailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString(),
                                    emailPenyewa=snap.child(Constant().KEY_EMAIL_PENYEWA).value.toString(),
                                    isi=snap.child(Constant().KEY_ISI).value.toString(),
                                    judul=snap.child(Constant().KEY_JUDUL).value.toString(),
                                    namaKos = snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                                    tanggal =  tanggalHariIni,
                                )
                                permintaanArrayList.add(permintaan)
                            }

                            adapter= PermintaanAdapter(permintaanArrayList, this@PermintaanFragment)
                            layoutManager=LinearLayoutManager(view.context)
                            binding.rvpermintaan.layoutManager=layoutManager
                            binding.rvpermintaan.adapter=adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }


    private fun terimaPermintaanBayarTransfer(view:View,dataPermintaan: Permintaan)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==dataPermintaan.idPermintaan)
                        {
                            snap.ref.removeValue()
                            idTransfer=UUID.randomUUID().toString()
                            buktiTransfer=BuktiTransfer(
                                idPemilik = dataPermintaan.idPemilik,
                                idPenyewa = dataPermintaan.idPenyewa,
                                idBuktiTransfer = idTransfer,
                                emailPemilik=dataPermintaan.emailPemilik,
                                emailPenyewa=dataPermintaan.emailPenyewa,
                                idKos = dataPermintaan.idKos,
                                namaKos = dataPermintaan.namaKos,
                                tanggal = tanggalHariIni,
                                urlBuktiTransfer = ""
                            )

                            database.child(Constant().KEY_BUKTI_TRANSFER)
                                .child(idTransfer)
                                .setValue(buktiTransfer)

                            Toast.makeText(view.context, "Sukses menerima permintaan pengguna, sekarang tunggu pengguna mengupload bukti transfer", Toast.LENGTH_SHORT).show()
                            val indexPermintaan = permintaanArrayList.indexOf(permintaan)
                            adapter.notifyItemRemoved(indexPermintaan)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB Error", error.message)
                }

            })
    }

    private fun terimaPermintaanBayarDitempat(view:View,dataPermintaan: Permintaan)
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
                                        idPenyewa=dataPermintaan.idPenyewa,
                                        tanggal=tanggalHariIni,
                                        idKos = dataPermintaan.idKos
                                    )

                                    transaksi=Transaksi(
                                        idTransaksi = UUID.randomUUID().toString(),
                                        idPemilik=dataPermintaan.idPemilik,
                                        idPenyewa = dataPermintaan.idPenyewa,
                                        isi="Permintaan Sewa ${dataPermintaan.namaKos} Dari ${dataPermintaan.emailPenyewa} Diterima",
                                        judul=Constant().KEY_PERMINTAAN_SEWA,
                                        tanggal=tanggalHariIni,
                                        tipe=Constant().KEY_TERIMA_SEWA
                                    )


                                    database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                                        .child(permintaan.idPenyewa)
                                        .push()
                                        .setValue(sewa)


                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPenyewa)
                                        .push()
                                        .setValue(transaksi)

                                    transaksi.isi="Anda Menerima Sewa ${permintaan.namaKos} Dari ${permintaan.emailPenyewa}"



                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPemilik)
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
                                                .child(dataPermintaan.idPenyewa)
                                                .push()
                                                .setValue(history)
                                        }


                                    Toast.makeText(activity, "Sukses Menerima Permintaan", Toast.LENGTH_SHORT).show()


                                    val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                    adapter.notifyItemRemoved(indexPermintaan)

                                }
                                .addOnFailureListener {error->
                                    Toast.makeText(view.context, "Gagal Menerima Permintaan", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db",error.message)
                }

            })
    }

    private fun terimaPermintaanKeluarKos(view:View,permintaanId: String)
    {
        database.child(Constant().KEY_PERMINTAAN)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {snap->
                        if(snap.child(Constant().KEY_ID_PERMINTAAN).value.toString()==permintaanId)
                        {
                            snap.ref.removeValue()

                            transaksi=Transaksi(
                                idTransaksi = UUID.randomUUID().toString(),
                                idPenyewa = idPengguna,
                                idPemilik = permintaan.idPemilik,
                                isi="Permintaan Keluar dari ${permintaan.namaKos} Diterima Oleh ${permintaan.emailPemilik} ",
                                judul=Constant().PERMINTAAAN_AKHIRI_SEWA,
                                tanggal=tanggalHariIni,
                                tipe=Constant().KEY_AKHIRI_SEWA
                            )

                            database.child(Constant().KEY_TRANSAKSI)
                                .child(permintaan.idPenyewa)
                                .push()
                                .setValue(transaksi)


                            transaksi.isi="Anda Menerima Permintan Keluar ${permintaan.emailPemilik} dari ${permintaan.namaKos}"

                            database.child(Constant().KEY_TRANSAKSI)
                                .child(permintaan.idPemilik)
                                .push()
                                .setValue(transaksi)

                            database.child(Constant().KEY_DAFTAR_KOS)
                                .child(permintaan.idKos)
                                .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                .setValue(ServerValue.increment(1))

                            database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                                .child(permintaan.idPenyewa)
                                .get().addOnSuccessListener {snap_sewa->
                                    val idKosSewa=snap_sewa.child(Constant().KEY_ID_KOS).value.toString()

                                    if(permintaan.idKos==idKosSewa)
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
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db",error.message)
                }

            })
    }

    private fun tolakPermintaan(view: View,permintaanId:String)
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
                                       idTransaksi=UUID.randomUUID().toString(),
                                       idPemilik = permintaan.idPemilik,
                                       idPenyewa = permintaan.idPenyewa,
                                       judul = "Permintaan Sewa Ditolak",
                                       isi = "Permintaan Sewa Anda Ditolak Oleh ${permintaan.emailPemilik}",
                                       tipe=Constant().KEY_TOLAK_SEWA,
                                       tanggal=tanggalHariIni
                                   )

                                   database.child(Constant().KEY_TRANSAKSI)
                                       .child(permintaan.idPenyewa)
                                       .push()
                                       .setValue(transaksi)

                                   transaksi.isi="Anda Menolak Permintaan Sewa Dari ${permintaan.emailPenyewa} untuk Kos ${permintaan.namaKos}"

                                   database.child(Constant().KEY_TRANSAKSI)
                                       .child(permintaan.idPemilik)
                                       .push()
                                       .setValue(transaksi)

                                   database.child(Constant().KEY_DAFTAR_KOS)
                                       .child(permintaan.idKos)
                                       .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                       .setValue(ServerValue.increment(1))

                                   Toast.makeText(activity, "Sukses Menolak Permintaan", Toast.LENGTH_SHORT).show()

                                   val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                   adapter.notifyItemRemoved(indexPermintaan)

                               }
                               .addOnFailureListener {
                                   Toast.makeText(view.context, "Gagal Menolak Permintaan", Toast.LENGTH_SHORT).show()
                               }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database Error",error.message)
                }

            })
    }


    private fun tolakPermintanAkhiriSewa(view:View, permintaanId: String)
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
                                        idTransaksi=UUID.randomUUID().toString(),
                                        idPemilik = permintaan.idPemilik,
                                        idPenyewa = permintaan.idPenyewa,
                                        judul = "Permintaan Akhiri Sewa Ditolak",
                                        isi = "Permintaan Akhiri Sewa Anda Ditolak Oleh ${permintaan.emailPemilik}",
                                        tipe=Constant().KEY_TOLAK_SEWA,
                                        tanggal=tanggalHariIni
                                    )

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPenyewa)
                                        .push()
                                        .setValue(transaksi)

                                    transaksi.isi="Anda Menolak Permintaan Akhiri Sewa Dari ${permintaan.emailPenyewa} untuk Kos ${permintaan.namaKos}"

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPemilik)
                                        .push()
                                        .setValue(transaksi)

                                    Toast.makeText(activity, "Sukses Menolak Permintaan Akhiri Sewa", Toast.LENGTH_SHORT).show()

                                    val indexPermintaan= permintaanArrayList.indexOf(permintaan)
                                    adapter.notifyItemRemoved(indexPermintaan)

                                }
                                .addOnFailureListener {
                                    Toast.makeText(view.context, "Gagal Menolak Permintaan", Toast.LENGTH_SHORT).show()
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
                                        idTransaksi=UUID.randomUUID().toString(),
                                        idPemilik = permintaan.idPemilik,
                                        idPenyewa =permintaan.idPenyewa,
                                        judul = "Permintaan Sewa Dibatalkan",
                                        isi = "Anda Membatalkan Permintaan Sewa Kos ${permintaan.namaKos}",
                                        tipe=Constant().KEY_BATAL_SEWA,
                                        tanggal=tanggalHariIni
                                    )

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPenyewa)
                                        .push()
                                        .setValue(transaksi)

                                    transaksi.isi="Penyewa ${permintaan.emailPenyewa}, Batal untuk menyewa ${permintaan.namaKos}"

                                    database.child(Constant().KEY_TRANSAKSI)
                                        .child(permintaan.idPemilik)
                                        .push()
                                        .setValue(transaksi)

                                    database.child(Constant().KEY_DAFTAR_KOS)
                                        .child(permintaan.idKos)
                                        .child(Constant().KEY_JUMLAH_KAMAR_KOS)
                                        .setValue(ServerValue.increment(1))


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


    private fun lihatProfilePengguna(view: View,idPengguna:String)
    {
        intent= Intent(view.context,RatingProfileActivity::class.java)
        intent.putExtra("idPengguna",idPengguna)
        startActivity(intent)
    }

    override fun onTerimaCLickListener(view: View, dataPermintaan: Permintaan) {
        if(dataPermintaan.judul==Constant().PERMINTAAAN_AKHIRI_SEWA)
        {
            terimaPermintaanKeluarKos(view,dataPermintaan.idPermintaan)
        }

        else if(dataPermintaan.judul==Constant().KEY_PERMINTAAN_SEWA)
        {

            database.child(Constant().KEY_DAFTAR_KOS)
                .child(permintaan.idKos)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val snapJenisBayarKos=snapshot.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString()
                        if(snapJenisBayarKos==Constant().KEY_TRANSFER)
                        {
                            terimaPermintaanBayarTransfer(view,dataPermintaan)
                        }

                        else if(snapJenisBayarKos==Constant().KEY_BAYARDITEMPAT)
                        {
                            terimaPermintaanBayarDitempat(view,dataPermintaan)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("DB Error", error.message)
                    }

                })
        }
    }

    override fun onTolakClickListener(view: View, dataPermintaan: Permintaan) {
        if(dataPermintaan.judul==Constant().PERMINTAAAN_AKHIRI_SEWA)
        {
            tolakPermintanAkhiriSewa(view, dataPermintaan.idPermintaan)
        }
        else
        {
            tolakPermintaan(view,dataPermintaan.idPermintaan)
        }
    }

    override fun onLihatProfileListener(view: View, dataPermintaan: Permintaan) {
        lihatProfilePengguna(view,dataPermintaan.idPenyewa)
    }

    override fun onBatalClickListener(view: View, dataPermintaan: Permintaan) {
        batalkanPermintaan(dataPermintaan.idPermintaan)
    }


}