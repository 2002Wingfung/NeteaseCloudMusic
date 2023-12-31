package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.SongsData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchInterface {
    @GET("search")
    fun getSearchData(@Query("keywords")keywords:String,@Query("limit") limit:Int,@Query("offset") offset:Int): retrofit2.Call<SongsData>
    @GET("search")
    fun getResponseBody(@Query("keywords")keywords:String,@Query("limit") limit:Int,@Query("offset") offset:Int): retrofit2.Call<ResponseBody>
}