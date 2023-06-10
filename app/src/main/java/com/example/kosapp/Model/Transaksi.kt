package com.example.kosapp.Model

data class Transaksi(
    val idTransaksi:String,
    val idPenyewa:String,
    val idPemilik:String,
    val tipe:String,
    val tanggal:String,
    val judul:String,
    var isi:String
)