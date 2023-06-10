package com.example.kosapp.Model

data class BuktiTransfer(val idBuktiTransfer:String,
                         val idPemilik:String,
                         val idPenyewa:String,
                         val emailPemilik:String,
                         val emailPenyewa:String,
                         val idKos:String,
                         val namaKos:String,
                         val urlBuktiTransfer:String,
                         val tanggal:String
                        )