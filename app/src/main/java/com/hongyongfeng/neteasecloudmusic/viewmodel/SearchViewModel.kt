package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.RequestBuilder
import com.hongyongfeng.neteasecloudmusic.testNew.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call

class SearchViewModel :BaseViewModel(){
    val testValue by lazy {
        MutableLiveData("")
    }
    /**
     * AUTHOR:hong
     * INTRODUCE:获取需要的Repository
     */
    private val repository by lazy {
        getRepository<TestRepository>()
    }
    fun clearTableSong(){

    }
}