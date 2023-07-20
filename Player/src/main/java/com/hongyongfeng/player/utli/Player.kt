package com.hongyongfeng.player.utli

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast


class Player {
    companion object{
        @JvmStatic

        fun initMediaPlayer(url :String,mediaPlayer:MediaPlayer){

            mediaPlayer.setDataSource(url) //设置播放来源

            //mediaPlayer.prepare()
            mediaPlayer.prepareAsync() //异步准备
            //seekBar.max=mediaPlayer.duration
            //发送广播

            mediaPlayer.setOnPreparedListener { mediaPlayer2 ->


                //异步准备监听
                Log.d("myPlayer","Voice异步文件准备完成")
                //mediaPlayer2.prepare()
                Log.d("myPlayer","Voice异步文件时长"+ (mediaPlayer2.duration / 1000).toString())
                mediaPlayer2.start() //播放
            }
            mediaPlayer.setScreenOnWhilePlaying(true) // 设置播放的时候一直让屏幕变亮


            mediaPlayer.setOnBufferingUpdateListener { mediaPlayer1, i ->
                Log.d("percent",i.toString())
                //文件缓冲监听
                //println("Voice进度: $i%")
                //mediaPlayer1.prepare()
                println("Voice文件长度"+ (mediaPlayer1.duration / 1000).toString())
            }
            mediaPlayer.setOnCompletionListener {
                Log.d("MediaPlayer","播放已完成,准备播放下一首")
                it.reset()
            }
        }
    }
}
