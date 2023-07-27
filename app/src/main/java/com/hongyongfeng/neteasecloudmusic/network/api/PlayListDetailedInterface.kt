package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.model.PlayListDetail
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListDetailedInterface {
    @GET("playlist/track/all")
    fun getPlayListDetailed(@Query("id") id:Long, @Query("limit") limit: Int, @Query("offset") offset:Int): retrofit2.Call<PlayListDetail>
    @GET("playlist/track/all")
    fun getSongResponseBody(@Query("id")id:Long,@Query("limit")limit:Int,@Query("offset")offset:Int): retrofit2.Call<ResponseBody>
}