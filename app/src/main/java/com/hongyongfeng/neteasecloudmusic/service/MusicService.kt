package com.hongyongfeng.neteasecloudmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.hongyongfeng.player.utli.Player

class MusicService : Service() {
    private val mediaPlayers=MediaPlayer()
    private val mBinder=MediaPlayerBinder()
    companion object {
        @JvmStatic
        var isChanging:Boolean=false
    }
    internal inner class MediaPlayerBinder : Binder() {
        fun getMusicService(): MusicService {
            return this@MusicService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        //TODO("Not yet implemented")

        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("MyService","onStartCommand")
        val url=intent?.getStringExtra("url")

        if (url != null) {
            Player.initMediaPlayer(url, mediaPlayers)
        }
//        refresh(seekBar,mediaPlayer)
        return super.onStartCommand(intent, flags, startId)
    }

    fun getMediaPlayer():MediaPlayer{
        return this.mediaPlayers
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayers.stop()
        mediaPlayers.release()
    }

}