package com.hongyongfeng.player

import android.media.MediaPlayer

class MyMediaPlayer(var mediaPlayer :MediaPlayer) {

    private var playPosition=0
    /**
     * 继续或者暂停
     */
    fun pauseOrContinueMusic(listener:OnFinishListener) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            listener.onPause()
        } else {
            mediaPlayer.start()
            listener.onContinue()
        }
        listener.onComplete()
    }
    /**
     * 下一首
     */
    fun nextMusic(position:Int,listSize:Int,default:Int,listener:PositionListener) {
        playPosition=position
        if (playPosition == -1) {
            playPosition = default
        }
        mediaPlayer.reset()
        if (playPosition >= listSize - 1) {
            playPosition = 0
        } else {
            playPosition += 1
        }
        listener.position(playPosition)
    }
    /**
     * 上一首
     */
    fun previousMusic(position:Int,listSize:Int,default:Int,listener:PositionListener) {
        playPosition=position
        if (playPosition == -1) {
            playPosition = default
        }
        if (playPosition == 0) {
            playPosition = listSize - 1
        } else {
            playPosition -= 1
        }
        listener.position(playPosition)
    }
}