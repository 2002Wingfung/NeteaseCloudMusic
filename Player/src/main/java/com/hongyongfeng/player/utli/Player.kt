package com.hongyongfeng.player.utli

import android.media.MediaPlayer
import android.util.Log


class Player {
    companion object{
        @JvmStatic

        fun initMediaPlayer(url :String,mediaPlayer:MediaPlayer){

            mediaPlayer.setDataSource(url) //设置播放来源

            mediaPlayer.prepareAsync() //异步准备

            mediaPlayer.setOnPreparedListener { mediaPlayer2 ->

                //异步准备监听
                println("Voice异步文件准备完成")
                println("Voice异步文件时长"+ (mediaPlayer.duration / 1000).toString())
                mediaPlayer2.start() //播放
            }
            mediaPlayer.setScreenOnWhilePlaying(true) // 设置播放的时候一直让屏幕变亮

            mediaPlayer.setOnBufferingUpdateListener { mediaPlayer1, i ->
                Log.d("percent",i.toString())
                //文件缓冲监听
                //println("Voice进度: $i%")
                println("Voice文件长度"+ (mediaPlayer1.duration / 1000).toString())
            }
        }
    }

}
