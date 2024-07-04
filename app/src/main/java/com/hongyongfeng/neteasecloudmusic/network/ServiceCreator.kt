package com.hongyongfeng.neteasecloudmusic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
//    const val BASE_URL="http://192.168.31.146:3000/"
    const val BASE_URL="http://music.bedofrosestll.cn/"
//    const val BASE_URL="http://192.168.34.239:3000/"
    //const val BASE_URL="https://neteasecloudmusicapi-sigma.vercel.app/"
    //const val BASE_URL="http://10.0.2.2/"
    private val retrofit=Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun <T> create(serviceClass: Class<T>):T =retrofit.create(serviceClass)

    inline fun <reified T> create1(): T = create(T::class.java)
}