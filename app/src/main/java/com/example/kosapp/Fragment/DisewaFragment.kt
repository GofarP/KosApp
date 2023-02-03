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
import com.example.kosapp.Activity.EditKosActivity
import com.example.kosapp.Activity.PenyewaActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter.ItemOnClickDisewa
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.FragmentDisewaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DisewaFragment : Fragment(), ItemOnClickDisewa {


    private lateinit var binding: FragmentDisewaBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter:DisewaAdapter
    private var kosArrayList=ArrayList<Kos>()
    private var auth=FirebaseAuth.getInstance().currentUser
    private var database= Firebase.database.reference
    private lateinit var kos:Kos
    private var emailPengguna=""
    private var storage=FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDisewaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData()
    {
        emailPengguna=auth?.email.toString()

        database.child(Constant().DAFTAR_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvdisewa.adapter=null


                    snapshot.children.forEach {snap->

                        if(snap.child(Constant().EMAIL_PEMILIK).value.toString()!=emailPengguna)
                        {
                            return@forEach
                        }

                        kos=Kos(
                            idKos=snap.child(Constant().ID_KOS).value.toString(),
                            alamat = snap.child(Constant().ALAMAT_KOS).value.toString(),
                            biaya = snap.child(Constant().BIAYA_KOS).value.toString().toDouble(),
                            emailPemilik=snap.child(Constant().EMAIL_PEMILIK).value.toString(),
                            gambarKos = snap.child(Constant().GAMBAR_KOS).value as ArrayList<String>,
                            gambarThumbnail = snap.child(Constant().GAMBAR_THUMBNAIL_KOS).value.toString(),
                            jenis=snap.child(Constant().JENIS_KOS).value.toString(),
                            jenisBayar = snap.child(Constant().JENIS_BAYAR_KOS).value.toString(),
                            lattitude = snap.child(Constant().LATTITUDE_KOS).value.toString(),
                            longitude = snap.child(Constant().LONGITUDE_KOS).value.toString(),
                            nama = snap.child(Constant().NAMA_KOS).value.toString(),
                            sisa = snap.child(Constant().JUMLAH_KAMAR_KOS).value.toString().toInt(),
                            fasilitas=snap.child(Constant().FASILITAS).value.toString(),
                            deskripsi=snap.child(Constant().DESKRIPSI).value.toString(),
                        )

                        kosArrayList.add(kos)

                    }

                    adapter= DisewaAdapter(kosArrayList,this@DisewaFragment)
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvdisewa.layoutManager=layoutManager
                    binding.rvdisewa.adapter=adapter
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun OnEditClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, EditKosActivity::class.java).putExtra("dataKos", dataKos)
        startActivity(intent)
    }

    override fun OnDeleteClick(v: View, dataKos: Kos) {
        emailPengguna= auth?.email.toString()
        database.child("daftarKos")
            .child(emailPengguna.replace(".",","))
            .child(dataKos.idKos)
            .removeValue()
            .addOnSuccessListener {

                kosArrayList.remove(kos)

                Toast.makeText(activity, "Sukses Menghapus ${dataKos.nama}", Toast.LENGTH_SHORT).show()

                storage.child(dataKos.gambarThumbnail).delete()

                for(i in dataKos.gambarKos.indices)
                {
                    storage.child(dataKos.gambarKos[i]).delete()
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onPeminjamClick(v: View, dataKos: Kos) {
        startActivity(Intent(activity, PenyewaActivity::class.java))
    }

}