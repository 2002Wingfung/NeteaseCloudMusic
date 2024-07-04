package com.hongyongfeng.neteasecloudmusic.network

import com.hongyongfeng.neteasecloudmusic.network.ServiceCreator.BASE_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RequestBuilder {
    private var retrofitBuilder:Retrofit
    init {
        OkHttpClient.Builder()
            .connectTimeout(10,TimeUnit.SECONDS)
            .build().apply {
                retrofitBuilder=Retrofit.Builder()
                    .baseUrl(REQUEST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(this)
                    .build()
            }
    }

    fun <T>getAPI(apiType:Class<T>):T{
        return retrofitBuilder.create(apiType)
    }
    companion object{
        const val REQUEST_URL=BASE_URL
    }
    //封装
    fun<T> getResponseFlow(requestFun:()->Response<T>): Flow<APIResponse<T>>  =
        flow{
            emit(APIResponse.Loading)
            try {
                with(requestFun()){
                    if (isSuccessful){
                        if (body()==null){
                            emit(APIResponse.Error("网络请求超时"))
                        }else{
                            emit(APIResponse.Success(body()))
                        }
                    }else{
                        emit(APIResponse.Error("网络请求失败"))
                    }
                }
            }catch (e:Exception){
                //println(e.toString())
                try {
                    emit(APIResponse.Error(e.message.toString()))
                }catch (e:Exception){
                    println(e.toString())
                }
            }

        }
}
//封装

sealed class APIResponse<out T>{//协变
    data class Success<out T>(val response:T):APIResponse<T>()
    data class Error(val errMsg:String):APIResponse<Nothing>()
    object Loading:APIResponse<Nothing>()//loading只是一个状态，不用存储数据
    //object是单例类
}