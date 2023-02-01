package com.example.kosapp.Model

import java.util.*

data class Permintaan(
    val idPermintaan:String,
    val idKos:String,
    val namaKos:String,
    val judul:String,
    val isi:String,
    val dari:String,
    val kepada:String,
    val tanggal:Date
)