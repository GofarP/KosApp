package com.example.kosapp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosapp.Adapter.RecyclerviewAdapter.HomeKosAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter.AdminItemOnClick
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.R
import com.example.kosapp.databinding.FragmentAkunBinding
import com.example.kosapp.databinding.LayoutDetailAkunBinding
import com.example.kosapp.databinding.LayoutDetailPenyewaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class AkunFragment : Fragment(), AdminItemOnClick {

    private lateinit var binding:FragmentAkunBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var akunAdapter:PenyewaAdapter
    private lateinit var pengguna: Pengguna
    private lateinit var kriteria:String
    private lateinit var keyword:String

    private var database=FirebaseDatabase.getInstance().reference
    private var akunArrayList=ArrayList<Pengguna>()
    private var cariAkunArrayList=ArrayList<Pengguna>()

    private val VIEW_ADMIN=1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAkunBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinner()

        binding.spnfilterakun.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                kriteria=binding.spnfilterakun.selectedItem.toString()

                if(kriteria=="Semua Akun")
                {
                    getAkunPengguna()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.textinputlayout.setEndIconOnClickListener {

            keyword=binding.txtsearchkos.text.toString()
            kriteria=binding.spnfilterakun.selectedItem.toString()
            cariAkunArrayList.clear()

            when(kriteria)
            {
                "Kelurahan"->{

                    for(i in akunArrayList.indices)
                    {
                        if(akunArrayList[i].kelurahan.lowercase().equals(keyword,ignoreCase=true))
                        {
                            cariAkunArrayList.add(akunArrayList[i])
                        }
                    }

                }

                "Kecamatan"->{
                    for(i in akunArrayList.indices)
                    {
                        if(akunArrayList[i].kecamatan.lowercase().equals(keyword, ignoreCase=true))
                        {
                            cariAkunArrayList.add(akunArrayList[i])
                        }
                    }
                }
            }

            akunAdapter= PenyewaAdapter(cariAkunArrayList,VIEW_ADMIN)
            layoutManager=LinearLayoutManager(activity)
            binding.rvakun.layoutManager=layoutManager
            binding.rvakun.adapter=akunAdapter

        }


    }

    private fun setSpinner()
    {
        val arrayFilterKos= arrayOf("Semua Akun","Kelurahan","Kecamatan")
        val jenisKosAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayFilterKos)
        binding.spnfilterakun.adapter=jenisKosAdapter
    }


    private fun getAkunPengguna()
    {
        database.child(Constant().KEY_USER)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {snap->
                        val role=snap.child(Constant().KEY_ROLE).value.toString()
                        val id=snap.child(Constant().KEY_ID_PENGGUNA).value.toString()
                        val nama=snap.child(Constant().KEY_USERNAME).value.toString()
                        val email=snap.child(Constant().KEY_EMAIL).value.toString()
                        val foto=snap.child(Constant().KEY_FOTO).value.toString()
                        val jenisKelamin=snap.child(Constant().KEY_JENIS_KELAMIN).value.toString()
                        val noTelp=snap.child(Constant().KEY_NOTELP).value.toString()
                        val nik=snap.child(Constant().KEY_NIK).value.toString()
                        val kelurahan=snap.child(Constant().KEY_KELURAHAN).value.toString()
                        val kecamatan=snap.child(Constant().KEY_KECAMATAN).value.toString()

                        Log.d("snap",snapshot.value.toString())

                        if(role!=Constant().KEY_ROLE_ADMIN)
                        {
                            pengguna=Pengguna(
                                id=id,
                                username = nama,
                                email=email,
                                foto=foto,
                                jenisKelamin = jenisKelamin,
                                noTelp = noTelp,
                                nik=nik,
                                kelurahan = kelurahan,
                                kecamatan = kecamatan
                            )

                            akunArrayList.add(pengguna)
                        }
                    }

                    akunAdapter= PenyewaAdapter(akunArrayList,VIEW_ADMIN)
                    akunAdapter.adminClickListener=this@AkunFragment
                    layoutManager=LinearLayoutManager(activity)
                    binding.rvakun.layoutManager=layoutManager
                    binding.rvakun.adapter=akunAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }
            })
    }


    private fun detailAkun(pengguna: Pengguna)
    {
        val dialogView=layoutInflater.inflate(R.layout.layout_detail_akun,null)
        val customDialog= AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .show()
        val customDialogBinding= LayoutDetailAkunBinding.inflate(layoutInflater)
        customDialog.setContentView(customDialogBinding.root)

        customDialogBinding.lbldetailnama.text=pengguna.username
        customDialogBinding.lbldetailjeniskelamin.text=pengguna.jenisKelamin
        customDialogBinding.lblldetailemail.text=pengguna.email
        customDialogBinding.lbldetailnotelp.text=pengguna.noTelp

    }

    override fun OnClickDetail(view: View, pengguna: Pengguna) {
        detailAkun(pengguna)
    }

}