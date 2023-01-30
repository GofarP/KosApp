package com.example.kosapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter
import com.example.kosapp.Adapter.RecyclerviewAdapter.PenyewaAdapter.ItemOnClick
import com.example.kosapp.Helper.Helper
import com.example.kosapp.Model.Pengguna
import com.example.kosapp.R
import com.example.kosapp.databinding.ActivityPenyewaBinding

class PenyewaActivity : AppCompatActivity(), ItemOnClick {

    private lateinit var binding:ActivityPenyewaBinding
    private var peminjamArrayList=ArrayList<Pengguna>()
    private lateinit var adapter: PenyewaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPenyewaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeLayoutDetail.titleactionbar.text="Daftar Penyewa"

        Helper().setStatusBarColor(this@PenyewaActivity)

        addData()
        adapter= PenyewaAdapter(peminjamArrayList, this)
        val layoutManager:RecyclerView.LayoutManager=LinearLayoutManager(this)
        binding.rvpenyewa.layoutManager=layoutManager
        binding.rvpenyewa.adapter=adapter
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

        peminjamArrayList.add(pengguna)

        pengguna=Pengguna(
            id="",
            email="put@ra.id",
            username = "Putra",
            foto="",
            jenisKelamin = "",
            noTelp = ""

        )

        peminjamArrayList.add(pengguna)

        pengguna=Pengguna(
            id="",
            email="Perdana@pd.com",
            username = "Perdana",
            foto="",
            jenisKelamin = "",
            noTelp = ""

        )

        peminjamArrayList.add(pengguna)

    }

    override fun OnClickDetail(view: View, pengguna: Pengguna) {
        Toast.makeText(this@PenyewaActivity, pengguna.username, Toast.LENGTH_SHORT).show()
    }

    override fun OnClickHapus(view: View, pengguna: Pengguna) {
        Toast.makeText(this@PenyewaActivity, "Menghapus ${pengguna.username}", Toast.LENGTH_SHORT).show()
    }

}