package com.example.kosapp.Callback

import com.example.kosapp.Model.Permintaan

interface PermintaanCallback{
    fun getData(arrayListPermintaan: ArrayList<Permintaan>)
}