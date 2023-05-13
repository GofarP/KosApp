package com.example.kosapp.Model

data class Pengguna(
    var id:String,
    var username:String,
    var email:String,
    var noTelp:String,
    var jenisKelamin:String,
    var foto:String,
    var nik:String,
    var kelurahan:String,
    var kecamatan:String,
    var role:String="",
)