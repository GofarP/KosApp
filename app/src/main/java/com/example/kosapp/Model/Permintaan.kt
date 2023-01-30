package com.example.kosapp.Model

import java.util.*

data class Permintaan(
    var id:String,
    var body:String,
    var dari:String,
    var kepada:String,
    var tanggal:Date
)