package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.RatingProfileAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.RatingProfile
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityRatingProfileBinding
import com.example.kosapp.databinding.LayoutRatingNullBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RatingProfileActivity : AppCompatActivity() {
    private lateinit var activityProfileBinding: ActivityRatingProfileBinding
    private lateinit var ratingProfileNullBinding: LayoutRatingNullBinding
    private lateinit var layoutManager:LayoutManager
    private lateinit var adapter:RatingProfileAdapter
    private lateinit var ratingProfile:RatingProfile
    private lateinit var idPengguna:String
    private var profileRatingArrayListProfile=ArrayList<RatingProfile>()
    private var auth=FirebaseAuth.getInstance()
    private  var extras:Bundle? = null
    private var database=FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding= ActivityRatingProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding.root)

        extras=intent.extras

        idPengguna = if(extras!=null) {
            extras!!.getString("idPengguna").toString()
        } else {
            auth.currentUser?.uid.toString()
        }

        getRatingProfile(idPengguna)
    }

//    private fun getData()
//    {
//        ratingProfile=
//            RatingProfile(idPengguna = "12345", idKos="54321", namaKos = "Kos Absi Jaya","Sangat Baik", "20-02-2023")
//        profileRatingArrayListProfile.add(ratingProfile)
//
//        layoutManager=LinearLayoutManager(this@RatingProfileActivity)
//        adapter= RatingProfileAdapter(ratingProfileArrayList = profileRatingArrayListProfile )
//        binding.rvratingprofile.layoutManager=layoutManager
//        binding.rvratingprofile.adapter=adapter
//
//    }

    private fun getRatingProfile(idPengguna: String)
    {
        database.child(Constant().KEY_RATING_USER)
            .child(idPengguna)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists())
                    {
                        snapshot.children.forEach {snap->
                            val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                            val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                            val ratingPengguna=snap.child(Constant().KEY_RATING_USER).value.toString()
                            val snapTanggal=snap.child(Constant().KEY_TANGGAL).value.toString()

                            ratingProfile=RatingProfile(
                                idPengguna=idPengguna,
                                idKos=snapIdKos,
                                namaKos=snapNamaKos,
                                ratingPengguna = ratingPengguna,
                                tanggal =snapTanggal
                            )

                            profileRatingArrayListProfile.add(ratingProfile)
                        }

                        layoutManager=LinearLayoutManager(this@RatingProfileActivity)
                        adapter= RatingProfileAdapter(profileRatingArrayListProfile)
                        activityProfileBinding.rvratingprofile.layoutManager=layoutManager
                        activityProfileBinding.rvratingprofile.adapter=adapter

                    }

                    else
                    {
                        ratingProfileNullBinding= LayoutRatingNullBinding.inflate(layoutInflater)
                        setContentView(ratingProfileNullBinding.root)
                        if(extras!=null)
                        {
                            ratingProfileNullBinding.lbldatanull.text="Pengguna Ini Belum Pernah Dapat Rating Sebelumnya"
                        }

                        else
                        {
                            ratingProfileNullBinding.lbldatanull.text="Anda Belum Pernah Dapat Rating Sebelumnya"
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB Error", error.message)
                }
            })
    }


}