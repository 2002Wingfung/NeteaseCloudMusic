package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.SongData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListDetailedInterface {
    @GET("playlist/track/all")
    fun getPlayListDetailed(@Query("id")id:String,@Query("limit")limit:String,@Query("offset")offset:String): retrofit2.Call<SongData>
    @GET("playlist/track/all")
    fun getSongResponseBody(@Query("id")id:String,@Query("limit")limit:String,@Query("offset")offset:String): retrofit2.Call<ResponseBody>
}