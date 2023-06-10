package com.example.kosapp.Model

import java.util.*

data class Permintaan(
    val idPermintaan:String,
    val idKos:String,
    val idPenyewa:String,
    val idPemilik:String,
    val emailPenyewa:String,
    val emailPemilik:String,
    val namaKos:String,
    val judul:String,
    val isi:String,
    val tanggal:String
)