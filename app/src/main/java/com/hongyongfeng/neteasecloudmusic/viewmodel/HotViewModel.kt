package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HotViewModel :ViewModel(){
    var editText=MutableLiveData<String>()
    fun setText(text:String){
        //val text=editText.value?:""
        editText.value=text
    }
    fun setTextNoMainThread(text:String){
        //val text=editText.value?:""
        editText.postValue(text)
    }
    fun clear(){
        editText.value=""
    }
}