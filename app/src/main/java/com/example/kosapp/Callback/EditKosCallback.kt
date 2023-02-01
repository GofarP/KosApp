package com.example.kosapp.Callback

import com.denzcoskun.imageslider.models.SlideModel

interface EditKosCallback {
    fun setImageList(arrayListImage:ArrayList<SlideModel>)
    fun setImageThumbnail(uri:String)
}