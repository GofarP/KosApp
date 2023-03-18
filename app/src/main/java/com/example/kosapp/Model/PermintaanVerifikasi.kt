package com.example.kosapp.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermintaanVerifikasi(
    val idPermintaan: String,
    val id:String,
    val email: String,
    val username:String,
    val judul:String,
    val isi:String,
    val tanggal:String
):Parcelable