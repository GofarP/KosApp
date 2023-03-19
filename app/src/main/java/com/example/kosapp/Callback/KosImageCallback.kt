package com.example.kosapp.Callback

import com.denzcoskun.imageslider.models.SlideModel
import com.example.kosapp.Model.GambarKos

interface KosImageCallback {
    fun setImageList(arrayListImageList:ArrayList<GambarKos>)
    fun setImageThumbnail(uri:String)
}