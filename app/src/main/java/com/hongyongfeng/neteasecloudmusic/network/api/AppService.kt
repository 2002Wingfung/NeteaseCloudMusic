package com.hongyongfeng.neteasecloudmusic.network.api

import com.hongyongfeng.neteasecloudmusic.model.Hot
import com.hongyongfeng.neteasecloudmusic.network.res.HotData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AppService {
    @GET("search/hot/detail")
    //@GET("abc.json")
    //fun getAppData(): retrofit2.Call<List<Hot>>
    fun getAppData(): retrofit2.Call<HotData>
    @GET("search/hot/detail")
    fun getResponseBody(): retrofit2.Call<ResponseBody>
}
interface Service{
    @GET("{page}/get_data.json")
    fun getData(@Path("page")page:Int): Call<Hot>
}
interface Example{
    @GET("get_data.json")
    fun getData(@Query("u")user:String,@Query("t")token:String): Call<Hot>
}
interface ExampleService {
    @DELETE("data/{id}")
    fun deleteData(@Path("id") id: String): Call<ResponseBody>
}
interface ExampleService1 {
    @POST("data/create")
    fun createData(@Body data: Hot): Call<ResponseBody>
}
interface ExampleService2 {
    @Headers("User-Agent: okhttp", "Cache-Control: max-age=0")
    @GET("get_data.json")
    fun getData(): Call<Hot>
}
interface ExampleService3 {
    @GET("get_data.json")
    fun getData(@Header("User-Agent") userAgent: String,
                @Header("Cache-Control") cacheControl: String): Call<Hot>
}