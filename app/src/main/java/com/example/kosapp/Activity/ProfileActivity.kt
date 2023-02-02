package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Kos
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityProfileBinding
import com.example.kosapp.databinding.LayoutConfirmPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    private lateinit var customDialogBinding: LayoutConfirmPasswordBinding
    private lateinit var customDialog: AlertDialog
    private lateinit var dialogView: View

    private var database= Firebase.database.reference
    private var auth=FirebaseAuth.getInstance()
    private var idPengguna=auth.currentUser?.uid.toString()
    private var emailPengguna=auth.currentUser?.email.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper().setStatusBarColor(this@ProfileActivity)


        binding.btngantiusername.setOnClickListener {
            val username=binding.txtgantiusernam.text.trim().toString()
            if(username.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Username Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }

            else
            {
                updateUsername(username)
            }
        }

        binding.btngantipassword.setOnClickListener {
            val password=binding.txtgantipassword.text.trim().toString()

            if(password.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }

            else
            {
                updatePassword(password)
            }
        }
    }


    private fun updateUsername(username:String)
    {
        dialogView=layoutInflater.inflate(R.layout.layout_confirm_password, null)
        customDialog=AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        customDialogBinding=LayoutConfirmPasswordBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        val password=customDialogBinding.txtpassword.text

        customDialogBinding.btnkonfirmasipassword.setOnClickListener {

            if(password.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Konfirmasi Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailPengguna,password.toString())
                .addOnSuccessListener {

                    database.child(Constant().USER)
                        .child(idPengguna)
                        .child(Constant().KEY_USERNAME)
                        .setValue(username)
                        .addOnSuccessListener {
                            Toast.makeText(this@ProfileActivity, "Sukses Mengubah Username", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ProfileActivity, "Gagal Mengubah Username", Toast.LENGTH_SHORT).show()
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(this@ProfileActivity, "Gagal Verifikasi Password", Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun updatePassword(passwordParam:String)
    {
        dialogView=layoutInflater.inflate(R.layout.layout_confirm_password, null)
        customDialog=AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        customDialogBinding=LayoutConfirmPasswordBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        val password=customDialogBinding.txtpassword.text

        customDialogBinding.btnkonfirmasipassword.setOnClickListener {

            if(password.isNullOrEmpty())
            {
                Toast.makeText(this@ProfileActivity, "Silahkan Isi Konfirmasi Password Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.currentUser?.updatePassword(passwordParam)
                ?.addOnSuccessListener {
                    Toast.makeText(this@ProfileActivity, "Sukses Mengubah Password", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    Log.d("error pass", it.message.toString())
                    Toast.makeText(this@ProfileActivity, "Gagal Mengubah Password", Toast.LENGTH_SHORT).show()
                }
        }

    }




}