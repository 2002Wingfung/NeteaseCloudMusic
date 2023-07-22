package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.PlayListData
import com.hongyongfeng.neteasecloudmusic.network.res.SongData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListInterface {
    @GET("user/playlist")
    fun getPlayList(@Query("uid")uid:String,@Query("limit")limit:String,@Query("offset")offset:String): retrofit2.Call<PlayListData>
    @GET("user/playlist")
    fun getSongResponseBody(@Query("uid")uid:String,@Query("limit")limit:String,@Query("offset")offset:String): retrofit2.Call<ResponseBody>
}