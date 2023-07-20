package com.hongyongfeng.neteasecloudmusic.ui.app

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityPlayerBinding
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayerInterface
import com.hongyongfeng.neteasecloudmusic.service.MusicService
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


public class PlayerActivity :BaseActivity<ActivityPlayerBinding,ViewModel>(
    ActivityPlayerBinding::inflate
){
    private val publicViewModel: PublicViewModel? by lazy{
        ViewModelProvider(this)[PublicViewModel::class.java]
    }
    private var albumId: Int=0
    private lateinit var mAnimator: ObjectAnimator
    private lateinit var mAnimatorNeedlePause: ObjectAnimator
    private lateinit var mAnimatorNeedleStart: ObjectAnimator
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var seekBar: SeekBar
    private var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myService: MusicService = (service as MusicService.MediaPlayerBinder).getMusicService()
            mediaPlayer = myService.getMediaPlayer()
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

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
        val albumIdResult=bundle?.getInt("albumId")
        if (albumIdResult!=null){
            albumId=albumIdResult
            picRequest(albumId)
        }
        val songId=bundle?.getInt("id")
        if (songId!=null){
            songsRequest(songId)
        }
        binding.tvSinger.text=bundle?.getString("singer")

        transparentNavBar(this)
        initView(binding, StatusBarUtils.getStatusBarHeight(this as AppCompatActivity)+5)
        initListener()

    // Object target:目标对象，
    // String propertyName:指定要改变对象的什么属性，这个属性名要求在对应对象中必须有对应的public的PsetPropertyName的方法。如上面的rotation就要求ImageView中必须有setRotation方法才行。
    // float... values:一系列这个属性将会到达的值
        mAnimator= ObjectAnimator.ofFloat(binding.imgAlbum, "rotation", 0f, 720f)
        val needle=binding.imgNeedle
        mAnimatorNeedlePause= ObjectAnimator.ofFloat(needle, "rotation", 0f, -30f)
        mAnimatorNeedleStart= ObjectAnimator.ofFloat(needle, "rotation", -30f, 0f)
        needle.pivotX=needle.width/1f+62
        needle.pivotY=needle.height*1f+135
        mAnimatorNeedlePause.duration = 700
        mAnimatorNeedlePause.interpolator = LinearInterpolator()
        mAnimatorNeedleStart.duration = 700
        mAnimatorNeedleStart.interpolator = LinearInterpolator()
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
        mAnimator.repeatCount = ObjectAnimator.INFINITE
        mAnimator.start()

        //当音频文件加载好之后就start

    // 在合适的位置调用 mAnimator.pause()方法进行暂停操作

    }
    private fun initView(binding: ActivityPlayerBinding, dp:Int){
        val lp = binding.layoutActionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutActionBar.layoutParams = lp
        seekBar=binding.seekBar
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
                            //println("Song:$url")
//                            Player.initMediaPlayer(url, mediaPlayer,seekBar)
                            val intent = Intent(this@PlayerActivity, MusicService::class.java)
                            intent.putExtra("url",url)

                            //Log.e("serviceMusic","Start")
                            bindService(
                                intent, mServiceConnection,
                                BIND_AUTO_CREATE
                            )
                            startService(intent)
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
                            //println("picUrl:$url")

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
            //mediaPlayer.prepare()
            println(mediaPlayer)
            if (count%2==0){
                handler.sendEmptyMessageDelayed(0, 700)
                mAnimatorNeedleStart.pause()

                mAnimatorNeedlePause.start()
                //"暂停".showToast(this)
                it.background=  getDrawable(R.drawable.ic_play_circle_2)
                if (mediaPlayer.isPlaying){

                    mediaPlayer.pause()//暂停播放
                }
            }else{
                handler.sendEmptyMessageDelayed(1, 700)
                mAnimatorNeedlePause.pause()
                mAnimatorNeedleStart.start()
                it.background=  getDrawable(R.drawable.ic_pause)
                if (!mediaPlayer.isPlaying){
                    mediaPlayer.start()//开始播放
                }
                //"播放".showToast(this)
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
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //MusicService.isChanging=true;

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                MusicService.isChanging=true;

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //当拖动停止后，控制mediaPlayer播放指定位置的音乐
                mediaPlayer.seekTo(seekBar!!.progress)
                MusicService.isChanging=false;

            }

        })
    }
    private fun refresh(seekBar: SeekBar, mediaPlayer:MediaPlayer){
        Timer().schedule(object : TimerTask(){
            override fun run() {
                if (!MusicService.isChanging){
                    //当用户正在拖动进度进度条时不处理进度条的的进度
                    seekBar.progress = mediaPlayer.currentPosition
                }
            }

        },0,10)
    }
    override fun onDestroy() {
        super.onDestroy()

//        mediaPlayer.stop()
//        mediaPlayer.release()
    }
}