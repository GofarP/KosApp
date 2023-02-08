package com.example.kosapp.Model

import java.util.*

data class Transaksi(
    val transaksiId:String,
    val tipe:String,
    val tanggal:String,
    val judul:String,
    val dari:String,
    val kepada:String,
    val isi:String
)