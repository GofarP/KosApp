package com.example.kosapp.Model

import java.util.*

data class History(
    val historyId:String,
    val tipe:String,
    val tanggal:Date,
    val judul:String,
    val dari:String,
    val kepada:String,
    val isi:String
)