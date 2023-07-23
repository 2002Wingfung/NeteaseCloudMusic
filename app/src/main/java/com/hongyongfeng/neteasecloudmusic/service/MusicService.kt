package com.hongyongfeng.neteasecloudmusic.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gsls.gt.GT
import com.hongyongfeng.neteasecloudmusic.*
import com.hongyongfeng.neteasecloudmusic.ActivityManager
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.receiver.NotificationClickReceiver
import com.hongyongfeng.neteasecloudmusic.ui.app.MainActivity
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.player.utli.Player
import kotlinx.coroutines.*


class MusicService : Service() {
    private var remoteViews: RemoteViews? = null

    private lateinit var musicReceiver:MusicReceiver
    private val mBinder = MediaPlayerBinder()
    private var isFirst = false

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
            UIControl(intent.action, TAG)
        }

        /**
         * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
         *
         * @param state 状态码
         * @param tag
         */
        private fun UIControl(state: String?, tag: String) {
            when (state) {
                PLAY -> Log.d(tag, "$PLAY or $PAUSE")
                PREV -> Log.d(tag, PREV)
                NEXT -> Log.d(tag, NEXT)
                CLOSE -> Log.d(tag, CLOSE)
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
        initRemoteViews()
        //注册动态广播
        registerMusicReceiver();
        showNotification()
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
//        isFirst=true
//        val url=intent?.getStringExtra("url")
//
//        if (url != null) {
//            Player.initMediaPlayer(url, mediaPlayer,{
//                //在Service服务类中发送广播消息给Activity活动界面
//                val intentBroadcastReceiver =Intent();
//                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;
//                sendBroadcast(intentBroadcastReceiver);
//            },{
//                val intentBroadcastReceiver =Intent();
//                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;
//                intentBroadcastReceiver.putExtra("percent",it)
//                sendBroadcast(intentBroadcastReceiver);
//            }){
//                val intentBroadcastReceiver =Intent();
//                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_COMPLETE;
//                sendBroadcast(intentBroadcastReceiver);
//            }
//        }
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("MyService", mediaPlayer.toString())
        val url = intent?.getStringExtra("url")

        if (url != null) {
            //if (!isFirst){
            Player().initMediaPlayer(url, mediaPlayer, {
                //在Service服务类中发送广播消息给Activity活动界面
                val intentBroadcastReceiver = Intent();
                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;

                sendBroadcast(intentBroadcastReceiver);
            }, {
                val intentBroadcastReceiver = Intent();
                intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_PERCENT;
                intentBroadcastReceiver.putExtra("percent", it)
                sendBroadcast(intentBroadcastReceiver);
            }) {

                //播放完成准备下一首
                //监听回调
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    //    fun getMediaPlayer():MediaPlayer{
//        return this.mediaPlayers
//    }
    override fun onDestroy() {

        runBlocking {
            launch {
                Log.e("MusicService","onDestroy")

                val songDao= AppDatabase.getDatabase(this@MusicService).songDao()
                songDao.updateIsPlaying(false, lastPlay = true)
//                for (song in songDao.loadAllSongs()){
//                    Log.e("MainActivity",song.toString()+"id:${song.id}")
//                }
            }
            launch {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
        super.onDestroy()
        if (musicReceiver != null) {
            //解除动态注册的广播
            unregisterReceiver(musicReceiver);
        }
        //将数据库表中的isPlaying字段设为false
        //开设一个字段lastSong
        //将最后一首播放的歌曲的lastSong设为true，用于底部播放器和通知栏播放器的显示
    }
    private var manager: NotificationManager? = null

    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initRemoteViews() {
        remoteViews = RemoteViews(this.packageName, R.layout.item_notification)

        //通知栏控制器上一首按钮广播操作
        val intentPrev = Intent(PREV)
        val prevPendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(this, 0, intentPrev, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getBroadcast(this, 0, intentPrev, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        //为prev控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent)

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        val intentPlay = Intent(PLAY)
        val playPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(this, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getBroadcast(this, 0, intentPlay, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        //为play控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent)

        //通知栏控制器下一首按钮广播操作
        val intentNext = Intent(NEXT)
        val nextPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(this, 0, intentNext, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getBroadcast(this, 0, intentNext, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        //为next控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent)

        //通知栏控制器关闭按钮广播操作
        val intentClose = Intent(CLOSE)
        val closePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(this, 0, intentClose, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getBroadcast(this, 0, intentClose, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        //为close控件注册事件
        remoteViews!!.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent)

    }
    /**
     * 显示通知
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val channelId = "play_control"
        val channelName = "播放控制"
        val importance = NotificationManager.IMPORTANCE_MAX
        createNotificationChannel(channelId, channelName, importance)
        //val remoteViews = RemoteViews(this.packageName, R.layout.notification)
        //点击整个通知时发送广播
        //点击整个通知时发送广播
        val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getBroadcast(applicationContext, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val currentActivity: Activity = ActivityManager.getCurrentActivity()!!
        val intent1 = Intent(Intent.ACTION_MAIN)
        intent1.addCategory(Intent.CATEGORY_LAUNCHER)
        intent1.setClass(this, currentActivity.javaClass)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //设置延迟意图
            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            PendingIntent.getActivity(this, 0, intent1,  PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification: Notification = NotificationCompat.Builder(this, "play_control")
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification))
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            //.setContentIntent(pi)
            .build()

        //发送通知
        manager!!.notify(1, notification)
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
}