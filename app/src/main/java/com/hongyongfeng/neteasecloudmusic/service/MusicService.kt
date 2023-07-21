package com.hongyongfeng.neteasecloudmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.hongyongfeng.neteasecloudmusic.ui.app.MainActivity
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.player.utli.Player

class MusicService : Service() {

    private val mBinder=MediaPlayerBinder()
    private var isFirst=false
    companion object {
        @JvmStatic
        var isChanging:Boolean=false
        @JvmStatic
        var mediaPlayer=MediaPlayer()
    }
    internal inner class MediaPlayerBinder : Binder() {
        fun getMusicService(): MusicService {
            return this@MusicService
        }
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
        Log.e("MyService",mediaPlayer.toString())
        val url=intent?.getStringExtra("url")

        if (url != null) {
            //if (!isFirst){
                Player().initMediaPlayer(url, mediaPlayer,{
                    //在Service服务类中发送广播消息给Activity活动界面
                    val intentBroadcastReceiver =Intent();
                    intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;

                    sendBroadcast(intentBroadcastReceiver);
                },{
                    val intentBroadcastReceiver =Intent();
                    intentBroadcastReceiver.action = PlayerActivity.ACTION_SERVICE_NEED;
                    intentBroadcastReceiver.putExtra("percent",it)
                    sendBroadcast(intentBroadcastReceiver);
                }){

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
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}