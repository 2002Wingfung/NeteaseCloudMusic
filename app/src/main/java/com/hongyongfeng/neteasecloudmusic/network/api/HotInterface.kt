package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.network.res.HotData
import retrofit2.http.GET

interface HotInterface {
    @GET("search/hot/detail")
    fun getHotData(): retrofit2.Call<HotData>
}