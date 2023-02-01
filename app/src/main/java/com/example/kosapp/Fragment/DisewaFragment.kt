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
import com.example.kosapp.Activity.DetailSewaKosActivity
import com.example.kosapp.Activity.EditKosActivity
import com.example.kosapp.Activity.PenyewaActivity
import com.example.kosapp.Activity.TestActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter.ItemOnClickDisewa
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
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
    private var userEmail=""
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


//    private fun getData()
//    {
//        userEmail=auth?.email.toString()
//        database.child(Constant().DAFTAR_KOS)
//            .addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    Log.d("debug",snapshot.value.toString())
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//    }

    private fun getData()
    {
        userEmail=auth?.email.toString()
        database.child("daftarKos")
            .child(userEmail.replace(".",","))
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()
                    binding.rvdisewa.adapter=null

                    snapshot.children.forEach {snap->
                        Log.d("snap",snap.value.toString())
                        kos=Kos(
                            id=snap.child("id").value.toString(),
                            alamat = snap.child("alamat").value.toString(),
                            biaya = snap.child("biaya").value.toString().toDouble(),
                            emailPemilik=snap.child("emailPemilik").value.toString(),
                            gambarKos = snap.child("gambarKos").value as ArrayList<String>,
                            gambarThumbnail = snap.child("gambarThumbnail").value.toString(),
                            jenis=snap.child("jenis").value.toString(),
                            jenisBayar = snap.child("jenisBayar").value.toString(),
                            lattitude = snap.child("lattitude").value.toString(),
                            longitude = snap.child("longitude").value.toString(),
                            nama = snap.child("nama").value.toString(),
                            sisa = snap.child("sisa").value.toString().toInt(),
                            fasilitas=snap.child("fasilitas").value.toString(),
                            deskripsi=snap.child("deskripsi").value.toString(),
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
        userEmail= auth?.email.toString()
        database.child("daftarKos")
            .child(userEmail.replace(".",","))
            .child(dataKos.id)
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