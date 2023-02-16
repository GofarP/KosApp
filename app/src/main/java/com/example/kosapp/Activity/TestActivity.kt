package com.example.kosapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Adapter.RecyclerviewAdapter.NegaraAdapter
import com.example.kosapp.Callback.EditKosCallback
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.databinding.ActivityTestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TestActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTestBinding
    private lateinit var adapter:NegaraAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private val database=Firebase.database.reference
    private val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var sliderArrayList=ArrayList<SlideModel>()
    private var storage=Firebase.storage.reference
    private lateinit var testIntent: Intent
    private lateinit var kos:Kos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testIntent=intent
        kos=testIntent.getParcelableExtra("dataKos")!!

//        anu(object : EditKosCallback{
//            override fun setImageList(gambarkosMap: HashMap<String, SlideModel>) {
//                TODO("Not yet implemented")
//            }
//
//
//            override fun setImageThumbnail(uri: String) {
//                TODO("Not yet implemented")
//            }
//
//        })

        binding.btntest.setOnClickListener {
            storage.child(kos.thumbnailKos).delete()
        }

//        binding.btncheck.setOnClickListener {
//            database.child(Constant().KEY_DAFTAR_KOS)
//                .child(kos.idKos)
//                .child(Constant().KEY_GAMBAR_KOS)
//                .addListenerForSingleValueEvent(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                       snapshot.children.forEachIndexed {i,snap->
//                           val urlGambarKos=snap.value.toString()
//                           if(kos.gambarKos[i] ==urlGambarKos)
//                           {
//                                Log.d("snap","sama")
//                           }
//
//                           else
//                           {
//                               Log.d("snap","Beda")
//                           }
//                       }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//        }


    }


//    private fun anu(editkosCallback:EditKosCallback)
//    {
//        kos.gambarKos.forEachIndexed { i, _ ->
//           storage.child(kos.gambarKos[i])
//               .downloadUrl.addOnSuccessListener {uri->
//                   sliderArrayList.add(SlideModel(uri.toString(),ScaleTypes.FIT))
//                   editkosCallback.setImageList(sliderArrayList)
//               }
//        }
//    }




    //        checkMenuChatData(object :ChatCallback{
//            override fun checkMenuChatData(dataMenuChatDitemukan: Boolean) {
//                if(dataMenuChatDitemukan)
//                {
//                    menuChatDataDitemukan=true
//
//                    if(emailSaatIni==arrayListMenuChat[0].emailPengirim)
//                    {
//                        binding.lblusername.text=arrayListMenuChat[0].usernamePenerima
//
//                        emailPenerima=arrayListMenuChat[0].emailPenerima
//
//                        storage.child(arrayListMenuChat[0].fotoPenerima).downloadUrl
//                            .addOnSuccessListener {uri->
//                                Glide.with(this@ChatActiviity)
//                                    .load(uri)
//                                    .into(binding.ivfotoprofil)
//                            }
//
//                    }
//
//                    else
//                    {
//                        binding.lblusername.text=arrayListMenuChat[0].usernamePengirim
//
//                        emailPenerima=arrayListMenuChat[0].emailPengirim
//
//                        storage.child(arrayListMenuChat[0].fotoPengirim).downloadUrl
//                            .addOnSuccessListener {uri->
//                                Glide.with(this@ChatActiviity)
//                                    .load(uri)
//                                    .into(binding.ivfotoprofil)
//                            }
//
//                    }
//
//                    getChatData()
//
//                }
//
//                else
//                {
//                    getProfilePengguna()
//                }
//
//            }
//
//        })


}