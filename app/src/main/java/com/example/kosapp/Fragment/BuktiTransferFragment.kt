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
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.BuktiTransferAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.ItemOnCLickLihatBukti
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.BuktiTransfer
import com.example.kosapp.Model.History
import com.example.kosapp.Model.Sewa
import com.example.kosapp.Model.Transaksi
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentPembayaranBinding
import com.example.kosapp.databinding.LayoutPopupBuktiTransferBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BuktiTransferFragment : Fragment(), ItemOnCLickLihatBukti {

    private lateinit var binding:FragmentPembayaranBinding
    private lateinit var bindingPopUpBuktiTransferBinding:LayoutPopupBuktiTransferBinding
    private lateinit var buktiTransfer: BuktiTransfer
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter:BuktiTransferAdapter
    private lateinit var dialogBuktiTransfer:View
    private lateinit var customDialog:AlertDialog
    private lateinit var sewa:Sewa
    private lateinit var transaksi:Transaksi
    private lateinit var tanggalHariIni:String
    private lateinit var history: History
    private lateinit var pathBuktiTransfer:String
    private lateinit var idBuktiTransfer: String
    private lateinit var idKos:String
    private lateinit var idPemilik:String
    private lateinit var idPenyewa:String
    private lateinit var idSewa:String
    private lateinit var tanggal:String
    private lateinit var idTransaksi:String
    private lateinit var emailPemilik:String
    private lateinit var emailPenyewa:String
    private lateinit var isiTransaksi:String


    private var database=FirebaseDatabase.getInstance().reference
    private var arrayListBuktiTransfer=ArrayList<BuktiTransfer>()
    private var firebaseStorage=FirebaseStorage.getInstance().reference
    private var uriBuktiTransfer: Uri?=null
    private var calendar=Calendar.getInstance()
    private var emailPengguna=FirebaseAuth.getInstance().currentUser?.email.toString()
    private var idPengguna=FirebaseAuth.getInstance().currentUser?.uid.toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPembayaranBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        getBuktiPembayaran(view)
    }

    private fun getBuktiPembayaran(view:View)
    {
        database.child(Constant().KEY_BUKTI_TRANSFER)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListBuktiTransfer.clear()
                    snapshot.children.forEach {snap->

                        val snapIdPemilik=snap.child(Constant().KEY_ID_PEMILIK).value.toString()
                        val snapIdPenyewa=snap.child(Constant().KEY_ID_PENYEWA).value.toString()
                        val snapEmailPemilik=snap.child(Constant().KEY_EMAIL_PEMILIK).value.toString()
                        val snapEmailPenyewa=snap.child(Constant().KEY_EMAIL_PENYEWA).value.toString()
                        val snapIdBuktiTransfer=snap.child(Constant().KEY_ID_BUKTI_TRANSFER).value.toString()
                        val snapIdKos=snap.child(Constant().KEY_ID_KOS).value.toString()
                        val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        val snapTanggal=snap.child(Constant().KEY_TANGGAL).value.toString()
                        val snapUrlGambarTransfer=snap.child(Constant().KEY_URL_BUKTI_TRANSFER).value.toString()

                        if(emailPengguna==snapEmailPemilik || emailPengguna==snapEmailPenyewa)
                        {
                            buktiTransfer=BuktiTransfer(
                                idPenyewa =snapIdPenyewa,
                                idPemilik = snapIdPemilik,
                                idBuktiTransfer = snapIdBuktiTransfer,
                                emailPemilik = snapEmailPemilik,
                                emailPenyewa=snapEmailPenyewa,
                                idKos=snapIdKos,
                                namaKos=snapNamaKos,
                                tanggal =snapTanggal,
                                urlBuktiTransfer =snapUrlGambarTransfer
                            )

                            arrayListBuktiTransfer.add(buktiTransfer)
                        }

                    }

                    layoutManager= LinearLayoutManager(view.context)
                    adapter= BuktiTransferAdapter(arrayListBuktiTransfer,this@BuktiTransferFragment)
                    binding.rvpembayaran.layoutManager=layoutManager
                    binding.rvpembayaran.adapter=adapter

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error db", error.message)
                }
            })
    }

    private fun uploadBuktiTransfer()
    {
        ImagePicker.with(requireActivity())
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                uploadBuktiTransferResult.launch(intent)
            }
    }

    private var uploadBuktiTransferResult:ActivityResultLauncher<Intent> =  registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
            result->

        if(result.resultCode == AppCompatActivity.RESULT_OK)
        {

            pathBuktiTransfer="${Constant().KEY_BUKTI_TRANSFER}/${idBuktiTransfer}/"

            dialogBuktiTransfer=layoutInflater.inflate(R.layout.layout_popup_bukti_transfer, null)
            customDialog=AlertDialog.Builder(requireActivity())
                .setView(dialogBuktiTransfer)
                .show()

            bindingPopUpBuktiTransferBinding= LayoutPopupBuktiTransferBinding.inflate(layoutInflater)
            customDialog.setContentView(bindingPopUpBuktiTransferBinding.root)

            bindingPopUpBuktiTransferBinding.btnpopupbatalkan.visibility=View.VISIBLE

            uriBuktiTransfer=result.data?.data
            bindingPopUpBuktiTransferBinding.ivbuktipembayaran.setImageURI(uriBuktiTransfer!!)


            bindingPopUpBuktiTransferBinding.btnpopup.setOnClickListener {


                database.child(Constant().KEY_DAFTAR_KOS)
                    .child(sewa.idKos).get()
                    .addOnSuccessListener {snap->
                        val snapAlamat=snap.child(Constant().KEY_ALAMAT_KOS).value.toString()
                        val snapNamaKos=snap.child(Constant().KEY_NAMA_KOS).value.toString()
                        val snapThumbnailKos=snap.child(Constant().KEY_GAMBAR_THUMBNAIL_KOS).value.toString()


                        history=History(
                            idHistory =UUID.randomUUID().toString(),
                            idKos = sewa.idKos,
                            alamat = snapAlamat,
                            tanggal = tanggalHariIni,
                            nama=snapNamaKos,
                            thumbnailKos =snapThumbnailKos
                        )

                        transaksi= Transaksi(
                            idTransaksi=idTransaksi,
                            idPemilik=idPemilik,
                            idPenyewa = idPenyewa,
                            isi=isiTransaksi,
                            judul = Constant().KEY_TERIMA_SEWA,
                            tanggal = tanggalHariIni,
                            tipe = Constant().KEY_PERMINTAAN_SEWA
                        )

                        database.child(Constant().KEY_TRANSAKSI)
                            .child(idPenyewa)
                            .push()
                            .setValue(transaksi)

                        transaksi.isi="Penyewa ${emailPenyewa} Telah Mengupload Bukti Transfer "

                        database.child(Constant().KEY_HISTORY)
                            .child(idPengguna)
                            .push()
                            .setValue(history)

                        database.child(Constant().KEY_DAFTAR_SEWA_KOS)
                            .push()
                            .setValue(sewa)

                        database.child(Constant().KEY_TRANSAKSI)
                            .push()
                            .setValue(transaksi)

                        firebaseStorage.child(pathBuktiTransfer).putFile(uriBuktiTransfer!!)

                        database.child(Constant().KEY_BUKTI_TRANSFER)
                            .child(buktiTransfer.idBuktiTransfer)
                            .child(Constant().KEY_URL_BUKTI_TRANSFER)
                            .setValue(pathBuktiTransfer)

                        Toast.makeText(requireActivity(), "Sukses Mengupload Bukti Transfer", Toast.LENGTH_SHORT).show()

                        customDialog.dismiss()

                    }
                    .addOnFailureListener {
                        Toast.makeText(requireActivity(), "Gagal Mengupload Bukti Transfer", Toast.LENGTH_SHORT).show()
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

    override fun OnClick(v: View, dataBuktiTransfer: BuktiTransfer) {

        if(buktiTransfer.urlBuktiTransfer=="")
        {
            idBuktiTransfer=dataBuktiTransfer.idBuktiTransfer

            idKos=dataBuktiTransfer.idKos
            idSewa=UUID.randomUUID().toString()
            tanggal=tanggalHariIni

            idTransaksi=UUID.randomUUID().toString()
            idPemilik=dataBuktiTransfer.idPemilik
            idPenyewa=dataBuktiTransfer.idPenyewa
            emailPemilik=dataBuktiTransfer.emailPemilik
            emailPenyewa=dataBuktiTransfer.emailPenyewa
            isiTransaksi="Permintaan Sewa ${dataBuktiTransfer.namaKos}  diterima oleh ${dataBuktiTransfer.emailPemilik}"

            uploadBuktiTransfer()
        }

        else
        {

            dialogBuktiTransfer=layoutInflater.inflate(R.layout.layout_popup_bukti_transfer, null)
            customDialog=AlertDialog.Builder(requireActivity())
                .setView(dialogBuktiTransfer)
                .show()

            bindingPopUpBuktiTransferBinding= LayoutPopupBuktiTransferBinding.inflate(layoutInflater)
            customDialog.setContentView(bindingPopUpBuktiTransferBinding.root)

            bindingPopUpBuktiTransferBinding.btnpopup.visibility=View.GONE

            firebaseStorage.storage.reference.child(dataBuktiTransfer.urlBuktiTransfer)
                .downloadUrl
                .addOnSuccessListener {url->
                    Glide.with(requireActivity())
                        .load(url)
                        .into(bindingPopUpBuktiTransferBinding.ivbuktipembayaran)
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


}