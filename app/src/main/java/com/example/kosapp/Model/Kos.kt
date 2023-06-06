package com.example.kosapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
 data class Kos
     (val idKos:String,
      val nama:String,
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
      val biaya:Double,
      val jenisBayar:String,
      val lattitude:String,
      val longitude:String,
      val status:String,
      val rating:Int?=null,
      val jarak:Double=0.0

      ):Parcelable