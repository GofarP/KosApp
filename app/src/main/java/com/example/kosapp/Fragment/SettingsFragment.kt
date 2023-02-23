package com.example.kosapp.Fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.SettingsAdapter.ItemOnClick
import com.example.kosapp.Activity.ProfileActivity
import com.example.kosapp.Activity.SigninActivity
import com.example.kosapp.Activity.TestActivity
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.PreferenceManager
import com.example.kosapp.databinding.FragmentSettingsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class SettingsFragment : Fragment(), ItemOnClick {

    private var settingList=ArrayList<String>()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var adapter:SettingsAdapter
    private var auth=FirebaseAuth.getInstance().currentUser
    private var storage=FirebaseStorage.getInstance().reference
    private var database=Firebase.database.reference
    private lateinit var preferenceManager:PreferenceManager
    private  var uri:Uri?=null
    private var imagePath:String=""
    private var newImagePath:String=""
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addSettings()
        getDataProfile()

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(view.context)

        binding.ivsettings.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent->
                    profileImagePickerResult.launch(intent)
                }
        }

    }

    private var profileImagePickerResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {result->
        if(result.resultCode==RESULT_OK)
        {
            uri=result.data?.data
            storage.child(imagePath).delete()

            newImagePath="${Constant().KEY_PROFILE_PICTURE}/${idPengguna}/${UUID.randomUUID()}"

            database.child(Constant().KEY_USER)
                .child(idPengguna)
                .child(Constant().KEY_FOTO)
                .setValue(newImagePath)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Sukses Mengganti Foto Profil", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Gagal Mengganti Foto Profil", Toast.LENGTH_SHORT).show()
                }

            storage.child(newImagePath).putFile(uri!!)

            binding.ivsettings.setImageURI(uri)

        }

        else if(result.resultCode== ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(activity, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSettings()
    {
        settingList.add("Profile")
        settingList.add("Logout")
        settingList.add("Test")
        adapter= SettingsAdapter(settingList,this)
        binding.rvsettings.layoutManager=LinearLayoutManager(activity)
        binding.rvsettings.adapter=adapter
    }

    private fun  getDataProfile()
    {
        val userId=auth?.uid

        database.child(Constant().KEY_USER)
            .orderByChild(Constant().KEY_ID_PENGGUNA)
            .equalTo(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {snap->
                    imagePath=snap.child(Constant().KEY_FOTO).value.toString()
                    binding.lblemail.text=snap.child(Constant().KEY_EMAIL).value.toString()
                    binding.lblusername.text=snap.child(Constant().KEY_USERNAME).value.toString()

                    storage.child(imagePath)
                        .downloadUrl
                        .addOnSuccessListener {url->
                            Glide.with(this@SettingsFragment)
                                .load(url)
                                .into(binding.ivsettings)

                        }
                        .addOnFailureListener {error->
                            Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                        }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onClick(view: View, settingsName: String) {
        when(settingsName)
        {
            "Profile"->{
                startActivity(Intent(activity, ProfileActivity::class.java))
            }

            "Logout"->{
                FirebaseAuth.getInstance().signOut()
                preferenceManager.clear()
                Toast.makeText(activity, "Sukses Logout", Toast.LENGTH_SHORT).show()
                activity?.finish()
                startActivity(Intent(activity,SigninActivity::class.java))
            }
            "Test"->{
                startActivity(Intent(activity,TestActivity::class.java))
            }
        }
    }

}