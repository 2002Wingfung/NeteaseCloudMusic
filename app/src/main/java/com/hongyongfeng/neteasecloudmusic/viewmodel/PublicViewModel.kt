package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.RequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call

/**
 * 多个Fragment都可以请求网络，可能有相同的代码块，比如request等
 * 都可以集成在PublicViewModel这个类里面，避免重复写同样的代码
 */

class PublicViewModel :ViewModel(){
    val testValue by lazy {
        MutableLiveData("")
    }
    private val requestBuilder= RequestBuilder()
    fun <T>getAPI(apiType:Class<T>):T= requestBuilder.getAPI(apiType)

    fun <T> Call<T>.getResponse(process: suspend (flow: Flow<APIResponse<T>>)->Unit){
        viewModelScope.launch (Dispatchers.IO){
            process(requestBuilder.getResponseFlow {
                this@getResponse.execute()//this特指getResponse的调用者而不是协程作用域
            })
        }

    }
}