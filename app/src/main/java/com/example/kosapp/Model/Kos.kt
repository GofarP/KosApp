package com.example.kosapp.Model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize


@Parcelize
 data class Kos
     (val idKos:String,
      val namaKos:String,
      val idPemilik:String,
      val emailPemilik:String,
      val jenis:String,
      val sisa:Int,
      val alamat:String,
      val kecamatan:String,
      val kelurahan:String,
      val thumbnailKos:String,
      val gambarKos:ArrayList<String>,
      val fasilitas:String,
      val deskripsi:String,
      val hargaHarian:Double,
      val hargaBulanan:Double,
      val hargaTahunan:Double,
      val jenisBayar:String,
      val lattitude:String,
      val longitude:String,
      val status:String,
      val rating:Int?=null,
      @get:Exclude
      val jarak:Double=0.0
      ):Parcelable