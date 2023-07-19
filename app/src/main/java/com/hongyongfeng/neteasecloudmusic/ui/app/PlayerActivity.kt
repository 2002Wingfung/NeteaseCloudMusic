package com.hongyongfeng.neteasecloudmusic.ui.app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityPlayerBinding
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayerInterface
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


public class PlayerActivity :BaseActivity<ActivityPlayerBinding,ViewModel>(
    ActivityPlayerBinding::inflate
){
    private val publicViewModel: PublicViewModel? by lazy{
        ViewModelProvider(this)[PublicViewModel::class.java]
    }
    private var albumId: Int=0
    private lateinit var mAnimator: ObjectAnimator

//    private var handler=Handler(Looper.getMainLooper()){
//
//    }
    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what==0){
                mAnimator.pause()
            }else{
                mAnimator.resume()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras;
        binding.tvTitle.text=bundle?.getString("name")
        val songId=bundle?.getInt("id")
        if (songId!=null){
            songsRequest(songId)
        }
        binding.tvId.text=songId.toString()
        binding.tvSinger.text=bundle?.getString("singer")

        val albumIdResult=bundle?.getInt("albumId")
        if (albumIdResult!=null){
            albumId=albumIdResult
            println(albumId)
            picRequest(albumId)
        }

        transparentNavBar(this)
        initView(binding, StatusBarUtils.getStatusBarHeight(this as AppCompatActivity)+5)
        initListener()

    // Object target:目标对象，
    // String propertyName:指定要改变对象的什么属性，这个属性名要求在对应对象中必须有对应的public的PsetPropertyName的方法。如上面的rotation就要求ImageView中必须有setRotation方法才行。
    // float... values:一系列这个属性将会到达的值
        mAnimator= ObjectAnimator.ofFloat(binding.imgAlbum, "rotation", 0f, 720f)
    // Object target:目标对象，
    // String propertyName:指定要改变对象的什么属性，这个属性名要求在对应对象中必须有对应的public的PsetPropertyName的方法。如上面的rotation就要求ImageView中必须有setRotation方法才行。
    // float... values:一系列这个属性将会到达的值
    // 设置一次动画的时间
    // 设置一次动画的时间
        mAnimator.duration = 19000
    // 设置插值器，用来控制变化率
    // 设置插值器，用来控制变化率
        mAnimator.interpolator = LinearInterpolator()
    // 设置重复的次数，无限
    // 设置重复的次数，无限
        mAnimator.repeatCount = ObjectAnimator.INFINITE
        mAnimator.start()
        mAnimator.setupStartValues()
        //当音频文件加载好之后就start

    // 在合适的位置调用 mAnimator.pause()方法进行暂停操作

    }
    private fun initView(binding: ActivityPlayerBinding, dp:Int){
        val lp = binding.layoutActionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutActionBar.layoutParams = lp
    }
    private fun songsRequest(songId:Int){
        //请求音频文件的代码
        //先获取音频的url，再进行解析
        publicViewModel!!.apply {
            getAPI(PlayerInterface::class.java).getSong(songId.toString()).getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAGInternet",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@PlayerActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val url=it.response.data[0].url
                            println("Song:$url")

                        }
                    }
                }
            }
        }
    }
    private fun picRequest(albumId:Int){

        //请求专辑图片的代码
        //先获取图片的url，再进行解析，然后转化成Bitmap
        publicViewModel!!.apply {
            getAPI(PlayerInterface::class.java).getAlbum(albumId.toString()).getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAGInternet",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@PlayerActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val url=it.response.songs[0].al.picUrl
                            println("picUrl:$url")

                            Picasso.get().load(url)
                                .into(binding.imgAlbum)
                        }
                    }
                }
            }
        }
    }
    private fun initListener() {
        var count=0
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.icPlay.setOnClickListener {
            if (count%2==0){

                handler.sendEmptyMessageDelayed(0, 500)

                "暂停".showToast(this)

            }else{
                handler.sendEmptyMessageDelayed(1, 500)

                "播放".showToast(this)
            }
            count++

        }
        binding.icNext.setOnClickListener {
            "next".showToast(this)

        }
        binding.icBack.setOnClickListener {
            "back".showToast(this)

        }
        binding.icMode.setOnClickListener {
            "mode".showToast(this)

        }
        binding.icList.setOnClickListener {
            "list".showToast(this)
        }
    }

}