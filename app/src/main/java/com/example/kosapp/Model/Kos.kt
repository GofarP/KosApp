package com.example.kosapp.Model

 data class Kos
     (val id:String,
      val nama:String,
      val jenis:String,
      val sisa:Int,
      val alamat:String,
      val gambarThumbnail:String,
      val gambarFasilitas:ArrayList<String>,
      val biaya:Double
      )