package com.hongyongfeng.player.utli

import android.media.AudioManager

import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import java.io.IOException


class MediaManager {
    /**
     * 播放控制方法 参数：需要播放音频的地址 & 听播放完成的监听
     */
    fun playSound(filePath: String?, onCompletionListener: OnCompletionListener?,onPreparedListener: OnPreparedListener,onBufferingUpdateListener: OnBufferingUpdateListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setOnErrorListener { mp, what, extra ->
                mMediaPlayer!!.reset()
                false
                //看看会不会有错误，如回调两次onCompletionListener
                //如果有错误则返回true
            }
        } else {
            mMediaPlayer!!.reset()
        }
        try {
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setOnCompletionListener(onCompletionListener)
            mMediaPlayer!!.setOnPreparedListener(onPreparedListener)
            mMediaPlayer!!.setOnBufferingUpdateListener(onBufferingUpdateListener)
            mMediaPlayer!!.setDataSource(filePath)
            mMediaPlayer!!.prepareAsync()
            mMediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //暂停播放控制
    fun pause() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            isPause = true
        }
    }

    //重新播放控制
    fun resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer!!.start()
            isPause = false
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    companion object {
        var mMediaPlayer: MediaPlayer? = null
        private var isPause = false
    }
}
