package com.example.kosapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.DetailKosSayaActivity
import com.example.kosapp.Activity.RouteJalanActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.MenyewaAdapter.*
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentMenyewaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MenyewaFragment : Fragment(), ItemOnCLickMenyewa {

    private lateinit var  binding:FragmentMenyewaBinding
    private var kosArrayList=ArrayList<Kos>()
    private lateinit var adapter: MenyewaAdapter
    private var database=Firebase.database.reference
    val emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    val idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    private lateinit var layoutManager:RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMenyewaBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataMenyewa()

    }



    private fun getDataMenyewa()
    {
        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
            .child(idPengguna)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvmenyewa.adapter=null

                    snapshot.children.forEach {snap->

                        val snapIdKosSewa=snap.child(Constant().KEY_ID_KOS).value.toString()

                        database.child(Constant().KEY_DAFTAR_KOS)
                            .child(snapIdKosSewa)
                            .get().addOnSuccessListener {snap_kos->
                                val kos=Kos(
                                    idKos=snap_kos.child(Constant().KEY_ID_KOS).value.toString(),
                                    alamat = snap_kos.child(Constant().KEY_ALAMAT_KOS).value.toString(),
                                    kecamatan=snap_kos.child(Constant().KEY_KECAMATAN).value.toString(),
                                    kelurahan=snap_kos.child(Constant().KEY_KELURAHAN).value.toString(),
                                    hargaHarian = snap_kos.child(Constant().KEY_HARGA_KOS_HARIAN).value.toString().toDouble(),
                                    hargaBulanan = snap_kos.child(Constant().KEY_HARGA_KOS_BULANAN).value.toString().toDouble(),
                                    hargaTahunan = snap_kos.child(Constant().KEY_HARGA_KOS_TAHUNAN).value.toString().toDouble(),
                                    idPemilik=snap_kos.child(Constant().KEY_ID_PEMILIK).value.toString(),
                                    emailPemilik=snap_kos.child(Constant().KEY_EMAIL_PEMILIK).value.toString(),
                                    gambarKos = snap_kos.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>,
                                    thumbnailKos = snap_kos.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString(),
                                    jenis=snap_kos.child(Constant().KEY_JENIS_KOS).value.toString(),
                                    jenisBayar = snap_kos.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString(),
                                    lattitude = snap_kos.child(Constant().KEY_LATTITUDE_KOS).value.toString(),
                                    longitude = snap_kos.child(Constant().KEY_LONGITUDE_KOS).value.toString(),
                                    namaKos = snap_kos.child(Constant().KEY_NAMA_KOS).value.toString(),
                                    sisa = snap_kos.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString().toInt(),
                                    fasilitas=snap_kos.child(Constant().KEY_FASILITAS).value.toString(),
                                    deskripsi=snap_kos.child(Constant().KEY_DESKRIPSI).value.toString(),
                                    status=snap_kos.child(Constant().KEY_STATUS_VERIFIKASI_AKUN).value.toString(),
                                    rating=snap_kos.child(Constant().KEY_RATING).value.toString().toInt()
                                )

                                kosArrayList.add(kos)

                                adapter= MenyewaAdapter(kosArrayList,this@MenyewaFragment)
                                layoutManager= LinearLayoutManager(activity)
                                binding.rvmenyewa.layoutManager=layoutManager
                                binding.rvmenyewa.adapter=adapter
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }


    override fun OnSelengkapnyaClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, DetailKosSayaActivity::class.java).putExtra("dataKos", dataKos)
        startActivity(intent)
    }

    override fun OnPetunjukClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, RouteJalanActivity::class.java)
        intent.putExtra("lattitude",dataKos.lattitude)
        intent.putExtra("longitude",dataKos.longitude)
        startActivity(intent)

    }


}