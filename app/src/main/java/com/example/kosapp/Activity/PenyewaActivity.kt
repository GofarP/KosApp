package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter.ItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityPenyewaBinding
import com.example.kosapp.databinding.LayoutDetailPenyewaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PenyewaActivity : AppCompatActivity(), ItemOnClick {

    private lateinit var binding:ActivityPenyewaBinding
    private var penyewaArrayList=ArrayList<Pengguna>()
    private lateinit var adapter: PenyewaAdapter
    private var database= Firebase.database.reference
    private lateinit var dataKosIntent: Intent
    private lateinit var kos:Kos
    private lateinit var pengguna: Pengguna
    private lateinit var  layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPenyewaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeLayoutDetail.titleactionbar.text="Daftar Penyewa"

        Helper().setStatusBarColor(this@PenyewaActivity)

        dataKosIntent=intent
        kos=dataKosIntent.getParcelableExtra("dataKos")!!

        getData()

    }


    fun getData()
    {
        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    penyewaArrayList.clear()
                    snapshot.children.forEach { snapSewa->

                        val snapSewaIdKos=snapSewa.child(Constant().KEY_ID_KOS).value.toString()
                        val snapSewaEmail=snapSewa.child(Constant().KEY_EMAIL).value.toString()

                        if(snapSewaIdKos==kos.idKos)
                        {
                            database.child(Constant().KEY_USER)
                                .addValueEventListener(object:ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach { snapUser->

                                            val snapUserEmail=snapUser.child(Constant().KEY_EMAIL).value.toString()

                                            if(snapUserEmail==snapSewaEmail)
                                            {
                                                pengguna= Pengguna(
                                                    id=snapUser.child(Constant().KEY_ID_PENGGUNA).value.toString(),
                                                    email=snapUser.child(Constant().KEY_EMAIL).value.toString(),
                                                    foto=snapUser.child(Constant().KEY_FOTO).value.toString(),
                                                    jenisKelamin = snapUser.child(Constant().KEY_JENIS_KELAMIN).value.toString(),
                                                    noTelp=snapUser.child(Constant().KEY_NOTELP).value.toString(),
                                                    username = snapUser.child(Constant().KEY_USERNAME).value.toString(),
                                                )

                                                penyewaArrayList.add(pengguna)
                                            }
                                        }

                                        adapter= PenyewaAdapter(penyewaArrayList,this@PenyewaActivity)
                                        layoutManager=LinearLayoutManager(this@PenyewaActivity)
                                        binding.rvpenyewa.layoutManager=layoutManager
                                        binding.rvpenyewa.adapter=adapter

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("error db",error.message)
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error", error.message)
                }

            })
    }

    fun addData()
    {
        var pengguna=Pengguna(
            id="",
            email="go@mail.com",
            username = "gofar",
            foto="",
            jenisKelamin = "",
            noTelp = ""

        )

        penyewaArrayList.add(pengguna)

        pengguna=Pengguna(
            id="",
            email="put@ra.id",
            username = "Putra",
            foto="",
            jenisKelamin = "",
            noTelp = ""

        )

        penyewaArrayList.add(pengguna)

        pengguna=Pengguna(
            id="",
            email="Perdana@pd.com",
            username = "Perdana",
            foto="",
            jenisKelamin = "",
            noTelp = ""

        )

        penyewaArrayList.add(pengguna)

    }

    override fun OnClickDetail(view: View, pengguna: Pengguna) {

        val dialogView=layoutInflater.inflate(R.layout.layout_detail_penyewa, null)
        val customDialog=AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        val customDialogBinding= LayoutDetailPenyewaBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        customDialogBinding.lbldetailnama.text=pengguna.username
        customDialogBinding.lbldetailemail.text=pengguna.email
        customDialogBinding.lblnotelp.text=pengguna.noTelp
        customDialogBinding.lbljeniskelamin.text=pengguna.jenisKelamin


    }


}