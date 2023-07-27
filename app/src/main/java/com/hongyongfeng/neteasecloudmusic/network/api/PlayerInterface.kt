package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.AlbumData
import com.hongyongfeng.neteasecloudmusic.network.res.SongData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayerInterface {
    @GET("album")
    fun getAlbum(@Query("id")id:String): retrofit2.Call<AlbumData>
    @GET("album")
    fun getAlbumResponseBody(@Query("id")id:String): retrofit2.Call<ResponseBody>

    @GET("song/url")
    fun getSong(@Query("id")id:String): retrofit2.Call<SongData>
    @GET("song/url")
    fun getSongResponseBody(@Query("id")id:String): retrofit2.Call<ResponseBody>
}