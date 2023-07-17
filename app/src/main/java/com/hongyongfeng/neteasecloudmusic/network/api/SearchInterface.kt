package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.HotData
import com.hongyongfeng.neteasecloudmusic.network.res.SongsData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchInterface {
    @GET("search")
    fun getSearchData(@Query("keywords")keywords:String): retrofit2.Call<SongsData>
    @GET("search")
    fun getResponseBody(@Query("keywords")keywords:String): retrofit2.Call<ResponseBody>
}