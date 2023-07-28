package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel
import com.hongyongfeng.neteasecloudmusic.model.entity.Hot
import com.hongyongfeng.neteasecloudmusic.model.repository.HotRepository
import com.hongyongfeng.neteasecloudmusic.model.repository.SearchRepository

class HotViewModel :BaseViewModel(){
    var editText=MutableLiveData<String>()
    private val repository by lazy {
        getRepository<HotRepository>()
    }
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
    fun loadAllHots():List<Hot>{
        return repository!!.loadAllHots()
    }
    fun insert(key:String){
        repository?.insert(key)
    }
}