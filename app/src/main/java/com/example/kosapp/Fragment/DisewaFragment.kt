package com.example.kosapp.Fragment

import android.app.AlertDialog
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Activity.EditKosActivity
import com.example.kosapp.Activity.PenyewaActivity
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.DisewaAdapter.ItemOnClickDisewa
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.*
import com.example.kosapp.Model.BiayaBukaKos
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentDisewaBinding
import com.example.kosapp.databinding.LayoutPopupBuktiTransferBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class DisewaFragment : Fragment(), ItemOnClickDisewa {


    private lateinit var binding: FragmentDisewaBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter:DisewaAdapter
    private lateinit var kos:Kos
    private lateinit var pathBuktiTransfer:String
    private lateinit var idBuktiTransfer:String
    private lateinit var dialogBuktiTransfer:View
    private lateinit var customDialog:AlertDialog
    private lateinit var bindingPopUpBuktiTransferBinding:LayoutPopupBuktiTransferBinding
    private lateinit var idKos:String
    private lateinit var history:History
    private lateinit var tanggalHariIni:String
    private lateinit var transaksi:Transaksi
    private lateinit var biayaBukaKos:com.example.kosapp.Model.BiayaBukaKos

    private var kosArrayList=ArrayList<Kos>()
    private var auth=FirebaseAuth.getInstance().currentUser
    private var database= Firebase.database.reference
    private var emailPengguna=""
    private var storage=FirebaseStorage.getInstance().reference
    private var uriBuktiTransfer: Uri?=null


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

        database.child(Constant().KEY_DAFTAR_KOS)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    kosArrayList.clear()

                    snapshot.children.forEach {snap->

                        if(snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()!=emailPengguna)
                        {
                            return@forEach
                        }

                        kos=Kos(
                            idKos=snap.child(Constant().KEY_ID_KOS).value.toString(),
                            alamat = snap.child(Constant().KEY_ALAMAT_KOS).value.toString(),
                            hargaHarian = snap.child(Constant().KEY_HARGA_KOS_HARIAN).value.toString().toDouble(),
                            hargaBulanan = snap.child(Constant().KEY_HARGA_KOS_BULANAN).value.toString().toDouble(),
                            hargaTahunan = snap.child(Constant().KEY_HARGA_KOS_TAHUNAN).value.toString().toDouble(),
                            kecamatan=snap.child(Constant().KEY_KECAMATAN).value.toString(),
                            kelurahan=snap.child(Constant().KEY_KELURAHAN).value.toString(),
                            idPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString(),
                            emailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString(),
                            gambarKos = snap.child(Constant().KEY_GAMBAR_KOS).value as ArrayList<String>,
                            thumbnailKos = snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString(),
                            jenis=snap.child(Constant().KEY_JENIS_KOS).value.toString(),
                            jenisBayar = snap.child(Constant().KEY_JENIS_BAYAR_KOS).value.toString(),
                            lattitude = snap.child(Constant().KEY_LATTITUDE_KOS).value.toString(),
                            longitude = snap.child(Constant().KEY_LONGITUDE_KOS).value.toString(),
                            namaKos = snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                            sisa = snap.child(Constant().KEY_JUMLAH_KAMAR_KOS).value.toString().toInt(),
                            fasilitas=snap.child(Constant().KEY_FASILITAS).value.toString(),
                            deskripsi=snap.child(Constant().KEY_DESKRIPSI).value.toString(),
                            rating=snap.child(Constant().KEY_RATING).value.toString().toInt(),
                            status=snap.child(Constant().KEY_STATUS_VERIFIKASI_AKUN).value.toString()
                        )

                        kosArrayList.add(kos)

                    }

                    adapter= DisewaAdapter(kosArrayList,this@DisewaFragment)
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvdisewa.layoutManager=layoutManager
                    binding.rvdisewa.adapter=adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    private fun deleteKos(idKos: String)
    {
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(Constant().KEY_DAFTAR_SEWA_KOS).exists()) {
                    snapshot.child(Constant().KEY_DAFTAR_SEWA_KOS).children.forEach { snap ->
                        if (snap.child(Constant().KEY_ID_KOS).value.toString() == idKos) {
                            Toast.makeText(
                                activity,
                                "Kos Sedang Disewa Tidak Dapat Dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            database.child(Constant().KEY_DAFTAR_KOS)
                                .child(idKos)
                                .removeValue()
                                .addOnSuccessListener {

                                    storage.child(kos.thumbnailKos).delete()

                                    kos.gambarKos.indices.forEach { i ->
                                        storage.child(kos.gambarKos[i]).delete()
                                    }


                                    Toast.makeText(
                                        activity,
                                        "Kos Sukses Dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val indexKos = kosArrayList.indexOf(kos)
                                    adapter.notifyItemRemoved(indexKos)

                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        activity,
                                        "Kos Gagal Dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }

                else
                {
                    database.child(Constant().KEY_DAFTAR_KOS)
                        .child(idKos)
                        .removeValue()
                        .addOnSuccessListener {

                            storage.child(kos.thumbnailKos).delete()
                                .addOnSuccessListener {
                                    Log.d("thumbnail kos","Sukses Menghapus Thumbnail KOs")
                                }
                                .addOnFailureListener {
                                    Log.d("thumbnail kos",it.message.toString())
                                }

                            kos.gambarKos.indices.forEach { i ->
                                storage.child(kos.gambarKos[i]).delete().addOnSuccessListener {
                                    Log.d("gambar kos","Sukses Menghapus Gambar KOs")
                                }.addOnFailureListener {
                                    Log.d("thumbnail kos",it.message.toString())
                                }
                            }

                            val indexKos = kosArrayList.indexOf(kos)
                            adapter.notifyItemRemoved(indexKos)

                            Toast.makeText(activity, "Sukses Menghapus Kos", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener {
                            Toast.makeText(activity, "Gagal Menghapus Kos", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db error", error.message)
            }

        })
    }


    private fun uploadBuktiTransfer(dataKos: Kos)
    {
        ImagePicker.with(requireActivity())
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                uploadBuktiTransferResult.launch(intent)
            }
    }

    private var uploadBuktiTransferResult: ActivityResultLauncher<Intent> =  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
            result->

        if(result.resultCode == AppCompatActivity.RESULT_OK)
        {
            idBuktiTransfer=UUID.randomUUID().toString()
            pathBuktiTransfer="${Constant().KEY_TRANSFER_BUKA_KOS}/${idBuktiTransfer}/"

            dialogBuktiTransfer=layoutInflater.inflate(R.layout.layout_popup_bukti_transfer, null)
            customDialog= AlertDialog.Builder(requireActivity())
                .setView(dialogBuktiTransfer)
                .show()

            bindingPopUpBuktiTransferBinding= LayoutPopupBuktiTransferBinding.inflate(layoutInflater)
            customDialog.setContentView(bindingPopUpBuktiTransferBinding.root)

            bindingPopUpBuktiTransferBinding.btnpopupbatalkan.visibility=View.VISIBLE

            uriBuktiTransfer=result.data?.data
            bindingPopUpBuktiTransferBinding.ivbuktipembayaran.setImageURI(uriBuktiTransfer!!)


            bindingPopUpBuktiTransferBinding.btnpopup.setOnClickListener {

                database.child(Constant().KEY_DAFTAR_KOS)
                    .child(kos.idKos)
                    .child(Constant().KEY_STATUS_VERIFIKASI_KOS)
                    .setValue(Constant().KEY_TERVERIFIKASI)
                    .addOnSuccessListener {

                        biayaBukaKos= BiayaBukaKos(
                            idKos=kos.idKos,
                            urlBuktiTransfer = pathBuktiTransfer,
                            namaKos = kos.namaKos
                        )

                        storage.child(pathBuktiTransfer).putFile(uriBuktiTransfer!!)

                        database.child(Constant().KEY_BIAYA_BUKA_KOS)
                            .child(kos.idKos)
                            .setValue(biayaBukaKos)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Sukses Mengunggah Bukti Transfer", Toast.LENGTH_SHORT).show()
                                customDialog.dismiss()
                            }

                    }.addOnFailureListener {
                        Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }

            bindingPopUpBuktiTransferBinding.btnpopupbatalkan.setOnClickListener {
                customDialog.dismiss()
            }
        }
        else if(result.resultCode== ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(requireActivity(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        }

    }


    override fun OnEditClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, EditKosActivity::class.java).putExtra(Constant().KEY_DATA_KOS, dataKos)
        startActivity(intent)
    }

    override fun OnDeleteClick(v: View, dataKos: Kos) {
        deleteKos(dataKos.idKos)
    }

    override fun onPeminjamClick(v: View, dataKos: Kos) {
        val intent=Intent(activity, PenyewaActivity::class.java).putExtra(Constant().KEY_DATA_KOS, dataKos)
        startActivity(intent)
    }

    override fun OnUploadBuktiTransfer(v: View, dataKos: Kos) {
        uploadBuktiTransfer(dataKos)
    }

}