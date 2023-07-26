package com.hongyongfeng.neteasecloudmusic.service

import LiveDataBus
import LiveDataBus.BusMutableLiveData
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.gsls.gt.GT
import com.hongyongfeng.neteasecloudmusic.*
import com.hongyongfeng.neteasecloudmusic.ActivityManager
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.RequestBuilder
import com.hongyongfeng.neteasecloudmusic.network.api.PlayerInterface
import com.hongyongfeng.neteasecloudmusic.ui.app.MainActivity
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.player.utli.Player
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import java.io.IOException
import kotlin.concurrent.thread


class MusicService : Service() {
    /**
     * 通知栏视图
     */
    private var remoteViews: RemoteViews? = null

    /**
     * 音乐播放器
     */
    private lateinit var musicReceiver: MusicReceiver
    private val mBinder = MediaPlayerBinder()
    private lateinit var prefs: SharedPreferences


    /**
     * 歌曲间隔时间
     */
    private val INTERNAL_TIME = 1000

    /**
     * 歌曲列表
     */
    private var mList: List<Song> = ArrayList()


    /**
     * 记录播放的位置
     */
    private var playPosition = -1

    /**
     * 通知
     */
    private var notification: Notification? = null

    /**
     * 通知ID
     */
    private val NOTIFICATION_ID = 1

    /**
     * 通知管理器
     */
    private var manager: NotificationManager? = null

    /**
     * 通知栏控制Activity页面UI
     */
    private var activityLiveData: BusMutableLiveData<String>? = null

    companion object {
        @JvmStatic
        var isChanging: Boolean = false

        @JvmStatic
        var mediaPlayer = MediaPlayer()
    }

    internal inner class MediaPlayerBinder : Binder() {
        fun getMusicService(): MusicService {
            return this@MusicService
        }
    }

    /**
     * 广播接收器 （内部类）
     */
    internal inner class MusicReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //UI控制
            uiControl(intent.action, TAG)
        }

        /**
         * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
         *
         * @param state 状态码
         * @param tag
         */
        private fun uiControl(state: String?, tag: String) {
            when (state) {
                PLAY -> {
                    Log.d(tag, "$PLAY or $PAUSE")
                    this@MusicService.pauseOrContinueMusic()
                }
                PREV -> {
                    Log.d(tag, PREV)
                    this@MusicService.previousMusic()
                }
                NEXT -> {
                    Log.d(tag, NEXT)
                    this@MusicService.nextMusic()
                }
                CLOSE -> {
                    Log.d(tag, CLOSE)
                    this@MusicService.closeNotification()
                }
                else -> {}
            }
        }

    }

    /**
     * 注册动态广播
     */
    private fun registerMusicReceiver() {
        musicReceiver = MusicReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(PLAY)
        intentFilter.addAction(PREV)
        intentFilter.addAction(NEXT)
        intentFilter.addAction(CLOSE)
        registerReceiver(musicReceiver, intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        activityLiveData = LiveDataBus.instance.with("activity_control", String::class.java)

        initRemoteViews()

        //注册动态广播
        registerMusicReceiver();
        initNotification()
        mList = songDao.loadAllSongs()

        //showGtNotification()
    }

    private fun showGtNotification() {
        //创建进自定义知栏
        //创建进自定义知栏
        val builder = GT.GT_Notification.createNotificationFoldView(
            this,
            com.gsls.gt.R.mipmap.ic_launcher_round, //通知栏图标
            R.layout.notification,  //折叠布局
            R.layout.item_notification2,  //展开布局
            true,  //单击是否取消通知
            true,  //是锁屏显示
            Intent(this, MainActivity::class.java),  //单击意图
            -1,  //发送通知时间
            222 //通知Id
        )

        //启动最终的通知栏

        //启动最终的通知栏
        GT.GT_Notification.startNotification(builder, 222)
    }

    override fun onBind(intent: Intent?): IBinder? {
        //TODO("Not yet implemented")
        prefs=getSharedPreferences("player", Context.MODE_PRIVATE)

        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("MyService", mediaPlayer.toString())
        mList = songDao.loadAllSongs()
        //每次启动服务的时候是更新播放列表的时候
        val url = intent?.getStringExtra("url")

        val position=intent?.getIntExtra("position",-1)
//        if (url != null) {
//            //if (!isFirst){
//            Player().initMediaPlayer(url, mediaPlayer, {
//                //在Service服务类中发送广播消息给Activity活动界面
//                val intentBroadcastReceiver = Intent();
//                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;
//                sendBroadcast(intentBroadcastReceiver);
//                val songDao = AppDatabase.getDatabase(this).songDao()
//                updateNotificationShow(songDao.loadId() - 1)
//            }, {
//                val intentBroadcastReceiver = Intent();
//                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_PERCENT;
//                intentBroadcastReceiver.putExtra("percent", it)
//                sendBroadcast(intentBroadcastReceiver);
//            }) {
//                //播放完成准备下一首
//                //监听回调
//
//            }
//        }
        play(position!!,0)
        return super.onStartCommand(intent, flags, startId)
    }

    //    fun getMediaPlayer():MediaPlayer{
//        return this.mediaPlayers
//    }
    override fun onDestroy() {
        runBlocking {
            launch {
                Log.e("MusicService", "onDestroy")

                val songDao = AppDatabase.getDatabase(this@MusicService).songDao()
                songDao.updateIsPlaying(false, lastPlay = true)
//                for (song in songDao.loadAllSongs()){
//                    Log.e("MainActivity",song.toString()+"id:${song.id}")
//                }
            }
            launch {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            launch {
                closeNotification()
                unregisterReceiver(musicReceiver)
            }

        }
        super.onDestroy()

        //将数据库表中的isPlaying字段设为false
        //开设一个字段lastSong
        //将最后一首播放的歌曲的lastSong设为true，用于底部播放器和通知栏播放器的显示
    }

    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initRemoteViews() {
        remoteViews = RemoteViews(this.packageName, R.layout.item_notification)

        //通知栏控制器上一首按钮广播操作
        val intentPrev = Intent(PREV)
        val prevPendingIntent: PendingIntent? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //设置延迟意图
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intentPrev,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            } else {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intentPrev,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        //为prev控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent)

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        val intentPlay = Intent(PLAY)
        val playPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(
                this,
                0,
                intentPlay,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        } else {
            PendingIntent.getBroadcast(
                this,
                0,
                intentPlay,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        //为play控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent)

        //通知栏控制器下一首按钮广播操作
        val intentNext = Intent(NEXT)
        val nextPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(
                this,
                0,
                intentNext,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        } else {
            PendingIntent.getBroadcast(
                this,
                0,
                intentNext,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        //为next控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent)

        //通知栏控制器关闭按钮广播操作
        val intentClose = Intent(CLOSE)
        val closePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(
                this,
                0,
                intentClose,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        } else {
            PendingIntent.getBroadcast(
                this,
                0,
                intentClose,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        //为close控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent)

    }

    /**
     * 显示通知
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotification() {
        val channelId = "play_control"
        val channelName = "播放控制"
        val importance = NotificationManager.IMPORTANCE_MAX
        createNotificationChannel(channelId, channelName, importance)
        //val remoteViews = RemoteViews(this.packageName, R.layout.notification)
        //点击整个通知时发送广播
        //点击整个通知时发送广播
//        val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
//        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            //设置延迟意图
//            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//
//        } else {
//            PendingIntent.getBroadcast(applicationContext, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT)
//        }
        val currentActivity: Activity = ActivityManager.getCurrentActivity()!!
        val intent1 = Intent(Intent.ACTION_MAIN)
        intent1.addCategory(Intent.CATEGORY_LAUNCHER)
        intent1.setClass(this, currentActivity.javaClass)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getActivity(
                this,
                0,
                intent1,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        } else {
            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        notification = NotificationCompat.Builder(this, "play_control")
            .setWhen(System.currentTimeMillis())
            //.setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification))
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()

        //发送通知
        //manager!!.notify(1, notification)
    }

    /**
     * 更改通知的信息和UI
     * @param position 歌曲位置
     */
    private fun updateNotificationShow(position: Int) {
        //manager!!.cancel(NOTIFICATION_ID)
        //播放状态判断
        if (mediaPlayer.isPlaying) {
            remoteViews!!.setImageViewResource(R.id.btn_notification_play, R.drawable.ic_pause_blue)
        } else {
            remoteViews!!.setImageViewResource(
                R.id.btn_notification_play,
                R.drawable.ic_play_circle_2_blue
            )
        }
        //歌曲名
        remoteViews!!.setTextViewText(R.id.Notification2Activity_music_name, mList[position].name)
        //歌手名
        remoteViews!!.setTextViewText(R.id.tv_singer, mList[position].artist)

        //发送通知
        manager!!.notify(NOTIFICATION_ID, notification)
        try {
            if (mList[position].albumUrl.isNullOrEmpty()){
                println("empty")
                songDao.loadLastPlayingSong()?.albumUrl.apply {
                    if (this.isNullOrEmpty()){
                        println("still empty")
                        //请求网络获取图片url
                        //getAPI()
                    }else{
                        Picasso.get()
                            .load(this)
                            .resize(128, 128)
                            .into(object : com.squareup.picasso.Target {
                                override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                                    remoteViews!!.setImageViewBitmap(R.id.img_album, bitmap)
                                    manager!!.notify(NOTIFICATION_ID, notification)
                                }
                                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                }
                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                }
                            })
                    }
                }
            }else{
                Picasso.get()
                    .load(mList[position].albumUrl)
                    .resize(128, 128)
                    .into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                            remoteViews!!.setImageViewBitmap(R.id.img_album, bitmap)
                            manager!!.notify(NOTIFICATION_ID, notification)
                        }
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        }
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }
                    })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.vibrationPattern = longArrayOf(0)
        channel.setSound(null, null)
        //获取系统通知服务
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager!!.createNotificationChannel(channel)
    }

    private val songDao = AppDatabase.getDatabase(this@MusicService).songDao()

    private val requestBuilder = RequestBuilder()
    private fun <T> getAPI(apiType: Class<T>): T = requestBuilder.getAPI(apiType)

    @OptIn(DelicateCoroutinesApi::class)
    fun <T> Call<T>.getResponse(process: suspend (flow: Flow<APIResponse<T>>) -> Unit) {

        GlobalScope.launch(Dispatchers.IO) {
            process(requestBuilder.getResponseFlow {
                this@getResponse.execute()//this特指getResponse的调用者而不是协程作用域
            })
        }
    }

    /**
     * 播放
     */
    fun play(position: Int,status:Int) {
        val prefs = getSharedPreferences("player", Context.MODE_PRIVATE)

        prefs.edit {
            putInt("songId", mList[position].songId.toInt())
        }
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            //监听音乐播放完毕事件，自动下一曲
            mediaPlayer.setOnCompletionListener {
                Log.d("MediaPlayer","播放已完成,准备播放下一首")
            }
        } else {
            mediaPlayer.setOnCompletionListener {

                Log.d("MediaPlayer","播放已完成,准备播放下一首")
                when (prefs.getInt("mode",-1)) {
                    0 -> {
                        //顺序
                        nextMusic()
                    }
                    1 -> {
                        //重复
                        play(playPosition, 0)
                    }
                    2 -> {
                        //随机

                    }
                }
            }
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                activityLiveData?.postValue("prepared")
                remoteViews!!.setImageViewResource(
                    R.id.btn_notification_play,
                    R.drawable.ic_pause_blue
                )
                manager?.notify(NOTIFICATION_ID,notification)
            }
            mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
                activityLiveData?.postValue(percent.toString())


            }
            mediaPlayer.setOnErrorListener { mp, what, extra ->
                //mediaPlayer.reset()
                println("错误")
                true
            }
        }
        //播放时 获取当前歌曲列表是否有歌曲
        mList = songDao.loadAllSongs()
        if (mList.isEmpty()) {
            return
        }
        try {
            //切歌前先重置，释放掉之前的资源
            mediaPlayer.reset()
            playPosition = position
            val songId = mList[position].songId
            thread {
                songDao.updateIsPlaying(false, lastPlay = true)
                songDao.updateLastPlaying(false, origin = true)
                songDao.updateLastPlayingById(true, position.toLong() + 1)
                songDao.updateIsPlaying(true, lastPlay = true)
            }
            getAPI(PlayerInterface::class.java).getAlbum(mList[position].albumId.toString())
                .getResponse { flow ->
                    flow.collect() {
                        when (it) {
                            is APIResponse.Error -> {
                                Log.e("TAGInternet", it.errMsg)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@MusicService, "网络连接错误", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            is APIResponse.Loading -> Log.e("TAG", "loading")
                            is APIResponse.Success -> withContext(Dispatchers.Main) {
                                val url = it.response.songs[0].al.picUrl
                                mList[position].albumUrl = url
                                songDao.updateLastPlaying(false, origin = true)
                                songDao.updateAlbumUrlAndLastPlaying(url, mList[position].albumId.toInt())

                                updateNotificationShow(position)
                                if (status==1){
                                    activityLiveData?.value=(PREV)
                                }else if (status==2){
                                    activityLiveData?.value=(NEXT)
                                }
                                activityLiveData?.postValue(PLAY)
                            }
                        }
                    }
                }
            getAPI(PlayerInterface::class.java).getSong(songId.toString()).getResponse { flow ->
                flow.collect() {
                    when (it) {
                        is APIResponse.Error -> {
                            Log.e("TAGInternet", it.errMsg)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MusicService, "网络连接错误", Toast.LENGTH_SHORT)
                                    .show()
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.pause()//暂停播放
                                }
                            }
                        }
                        is APIResponse.Loading -> Log.e("TAG", "loading")
                        is APIResponse.Success -> withContext(Dispatchers.Main) {
                            val url = it.response.data[0].url
                            //设置播放音频的资源路径
                            mediaPlayer.setDataSource(url)
                            mediaPlayer.prepareAsync()
                            //显示通知
                            updateNotificationShow(position)
                            activityLiveData?.postValue(PLAY)
                        }
                    }
                }

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 上一首
     */
    fun previousMusic() {
        if (playPosition == -1) {
            playPosition = ((songDao.loadLastPlayingSong()?.id?.toInt())?.minus(1)) ?: 0
        }
        if (playPosition == 0) {
            playPosition = mList.size - 1
        } else {
            playPosition -= 1
        }
        activityLiveData?.postValue(PREV)
        play(playPosition,1)
    }

    /**
     * 下一首
     */
    fun nextMusic() {
        if (playPosition == -1) {
            playPosition = ((songDao.loadLastPlayingSong()?.id?.toInt())?.minus(1)) ?: 0
        }
        mediaPlayer.reset()
        if (playPosition >= mList.size - 1) {
            playPosition = 0
        } else {
            playPosition += 1
        }
        activityLiveData?.postValue(NEXT)
        play(playPosition,2)
    }

    /**
     * 暂停/继续 音乐
     */
    fun pauseOrContinueMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            activityLiveData?.postValue(PAUSE)
            thread {
                songDao.updateIsPlaying(false, lastPlay = true)
            }
        } else {
            mediaPlayer.start()
            activityLiveData?.postValue(PLAY)

            thread {
                songDao.updateIsPlaying(true, lastPlay = true)
            }
        }
        if (playPosition == -1) {
            playPosition = ((songDao.loadLastPlayingSong()?.id?.toInt())?.minus(1)) ?: 0
        }
        //更改通知栏播放状态
        updateNotificationShow(playPosition)
    }

    /**
     * 关闭音乐通知栏
     */
    fun closeNotification() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        manager!!.cancel(NOTIFICATION_ID)
        activityLiveData?.postValue(CLOSE)
    }
}