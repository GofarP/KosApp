package com.example.kosapp.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermintaanVerifikasiKos(
    val idPermintaan: String,
    val idPemohon:String,
    val idKos: String,
    val email: String,
    val username:String,
    val judul:String,
    val isi:String,
    val tanggal:String
): Parcelable