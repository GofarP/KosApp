package com.example.kosapp.Fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Adapter.RecyclerviewAdapter.BuktiTransferAdminAdapter
import com.example.kosapp.Helper.Constant
import com.example.kosapp.databinding.FragmentAdminBuktiTransferBinding
import com.google.firebase.database.FirebaseDatabase
import com.example.kosapp.Model.BiayaBukaKos
import com.example.kosapp.R
import com.example.kosapp.databinding.LayoutPopupBuktiTransferBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AdminBuktiTransferFragment : Fragment(), BuktiTransferAdminAdapter.ItemOnCLickLihatBukti {

    private var arrayListBiayaBukaKos=ArrayList<BiayaBukaKos>()
    private var database=FirebaseDatabase.getInstance().reference
    private var storage=FirebaseStorage.getInstance().reference


    private lateinit var binding:FragmentAdminBuktiTransferBinding
    private lateinit var biayaBukaKos:BiayaBukaKos
    private lateinit var adapter:BuktiTransferAdminAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var dialogBuktiTransfer:View
    private lateinit var customDialog: AlertDialog
    private lateinit var bindingPopUpBuktiTransferBinding: LayoutPopupBuktiTransferBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdminBuktiTransferBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData()
    {
        database.child(Constant().KEY_BIAYA_BUKA_KOS)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { snap->
                        biayaBukaKos=BiayaBukaKos(
                            idKos = snap.child(Constant().KEY_ID_KOS).value.toString(),
                            namaKos = snap.child(Constant().KEY_NAMA_KOS).value.toString(),
                            urlBuktiTransfer = snap.child(Constant().KEY_URL_BUKTI_TRANSFER).value.toString()
                        )

                        arrayListBiayaBukaKos.add(biayaBukaKos)
                    }

                    adapter= BuktiTransferAdminAdapter(arrayListBiayaBukaKos,this@AdminBuktiTransferFragment)
                    layoutManager= LinearLayoutManager(activity)
                    binding.rvadminbuktitransfer.layoutManager=layoutManager
                    binding.rvadminbuktitransfer.adapter=adapter


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("database error",error.message)
                }

            })
    }

    private fun showBuktiBayar(biayaBukaKos: BiayaBukaKos)
    {
        dialogBuktiTransfer=layoutInflater.inflate(R.layout.layout_popup_bukti_transfer, null)
        customDialog= AlertDialog.Builder(requireActivity())
            .setView(dialogBuktiTransfer)
            .show()
        bindingPopUpBuktiTransferBinding= LayoutPopupBuktiTransferBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingPopUpBuktiTransferBinding.root)

        bindingPopUpBuktiTransferBinding.btnpopup.visibility=View.INVISIBLE
        bindingPopUpBuktiTransferBinding.btnpopupbatalkan.visibility=View.INVISIBLE

        storage.child(biayaBukaKos.urlBuktiTransfer).downloadUrl.addOnSuccessListener {url->
            Glide.with(context!!)
                    .load(url)
                    .into(bindingPopUpBuktiTransferBinding.ivbuktipembayaran)
        } .addOnFailureListener {
                Toast.makeText(activity, "Image Error", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDetailBuktiPembayaran(v: View, biayaBukaKos: BiayaBukaKos) {
        showBuktiBayar(biayaBukaKos)
    }

}