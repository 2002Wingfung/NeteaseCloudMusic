package com.hongyongfeng.player.utli

import android.media.MediaPlayer
import android.util.Log


class Player {
    companion object{


    }
    fun initMediaPlayer(url :String,mediaPlayer:MediaPlayer,listener:()->Unit,listener2: (i:Int) -> Unit,listener3: () -> Unit){

        mediaPlayer.setDataSource(url) //设置播放来源

        mediaPlayer.prepareAsync() //异步准备

        mediaPlayer.setOnPreparedListener { mediaPlayer2 ->


            //发送广播
            //异步准备监听
            Log.d("myPlayer","Voice异步文件准备完成")
            Log.d("myPlayer","Voice异步文件时长"+ (mediaPlayer2.duration / 1000).toString())
            mediaPlayer2.start() //播放
            listener()
        }
        mediaPlayer.setScreenOnWhilePlaying(true) // 设置播放的时候一直让屏幕变亮


        mediaPlayer.setOnBufferingUpdateListener { mediaPlayer1, i ->
            listener2(i)
            Log.d("percent",i.toString())
            //文件缓冲监听
            //println("Voice进度: $i%")
            //mediaPlayer1.prepare()
            println("Voice文件长度"+ (mediaPlayer1.duration / 1000).toString())
        }
        mediaPlayer.setOnCompletionListener {
            listener3()
            Log.d("MediaPlayer","播放已完成,准备播放下一首")
            it.reset()

        }
        mediaPlayer.setOnErrorListener{ _: MediaPlayer, _: Int, _: Int ->
            true
        }
    }
}
