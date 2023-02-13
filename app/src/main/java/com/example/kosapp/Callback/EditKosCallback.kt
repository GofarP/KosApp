package com.example.kosapp.Callback

import com.denzcoskun.imageslider.models.SlideModel

interface EditKosCallback {
    fun setImageList(arrayListImageList:ArrayList<SlideModel>)
    fun setImageThumbnail(uri:String)
}