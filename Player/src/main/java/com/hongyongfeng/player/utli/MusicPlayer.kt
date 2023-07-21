package com.hongyongfeng.player.utli

import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import java.io.IOException


/**
 * 音频播放控制类
 */
class MusicPlayer private constructor() : OnPreparedListener, OnCompletionListener,
    OnErrorListener, OnBufferingUpdateListener {
    /**
     * 获取当前状态
     *
     * @return
     */
    var state: Int
    private val mSampleTime: Int
    var mMediaPlayer: MediaPlayer? = null
    private var mAudioPlayerListener: AudioPlayerListener? = null
    private var mHandler: Handler? = null
    private val mUndatePlayPositionRunnable = Runnable { undatePosition() }
    var path: String? = null

    fun setmAudioPlayerListener(mAudioPlayerListener: AudioPlayerListener?) {
        this.mAudioPlayerListener = mAudioPlayerListener
    }

    init {
        state = STATE_UNINITIALIZED
        mSampleTime = 100
        if (Looper.myLooper() == null) {
            Looper.prepare()
            mHandler = Handler()
            Looper.loop()
        } else {
            mHandler = Handler()
        }
    }

    /**
     * 初始化MediaPlayer
     */
    fun init(listener: AudioPlayerListener?) {
        if (state == STATE_UNINITIALIZED) {
            if (listener == null) {
                throw RuntimeException("AudioPlayerListener not null")
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer()
            }
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setOnPreparedListener(this)
            mMediaPlayer!!.setOnCompletionListener(this)
            mMediaPlayer!!.setOnBufferingUpdateListener(this)
            mMediaPlayer!!.setOnErrorListener(this)
            state = STATE_INITIALIZED
        }
        mAudioPlayerListener = listener
    }

    val isPlaying: Boolean
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else {
            false
        }

    /**
     * 设置音频源（异步）
     *
     * @param path
     * @return 返回Duration
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws IOException
     */
    @Throws(
        IllegalArgumentException::class,
        SecurityException::class,
        IllegalStateException::class,
        IOException::class
    )
    fun setDataSource(path: String?): Boolean {
        this.path = path
        if (state == STATE_UNINITIALIZED) {
            throw RuntimeException("设置音频源之前请进行初始化")
        }
        if (state == STATE_PAUSE || state == STATE_PLAYING) {
            stop()
        }
        if (TextUtils.isEmpty(path)) {
            return false
        }
        mMediaPlayer!!.reset()
        mMediaPlayer!!.setDataSource(path)
        mMediaPlayer!!.prepareAsync()
        return true
    }

    /**
     * 设置音频源（异步）
     *
     * @param fileDescriptor
     * @return 返回Duration
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws IOException
     */
    @Throws(
        IllegalArgumentException::class,
        SecurityException::class,
        IllegalStateException::class,
        IOException::class
    )
    fun setDataSource(fileDescriptor: AssetFileDescriptor?): Boolean {
        if (state == STATE_UNINITIALIZED) {
            throw RuntimeException("设置音频源之前请进行初始化")
        }
        if (state == STATE_PAUSE || state == STATE_PLAYING) {
            stop()
        }
        if (fileDescriptor == null) {
            return false
        }
        mMediaPlayer!!.reset()
        mMediaPlayer!!.setDataSource(
            fileDescriptor.fileDescriptor, fileDescriptor.startOffset,
            fileDescriptor.length
        )
        mMediaPlayer!!.prepareAsync()
        return true
    }

    @Throws(IllegalStateException::class, IOException::class)
    fun play() {
        if (state == STATE_UNINITIALIZED) {
            throw RuntimeException("播放前请进行初始化")
        }
        if (state == STATE_INITIALIZED) {
            throw RuntimeException("播放前请设置音频源")
        }
        if (!mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.start()
            state = STATE_PLAYING
            undatePosition()
        }
    }

    fun pause() {
        try {
            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                state = STATE_PAUSE
            }
        } catch (e: Exception) {
        }
    }

    fun stop() {
        if (mMediaPlayer != null && (mMediaPlayer!!.isPlaying || state == STATE_PAUSE)) {
            mMediaPlayer!!.stop()
            state = STATE_STOP
        }
    }

    fun release() {
        state = STATE_UNINITIALIZED
        stop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
        }
        mMediaPlayer = null
        mAudioPlayerListener = null
        mInstance = null
    }

    fun seekTo(msec: Int) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.seekTo(msec)
        }
    }

    /**
     * 获取当前播放位置
     *
     * @return
     */
    val currentPostion: Int
        get() = if (mMediaPlayer == null) -1 else mMediaPlayer!!.currentPosition

    /**
     * 获取总时间
     *
     * @return
     */
    val duration: Int
        get() = if (mMediaPlayer == null) -1 else mMediaPlayer!!.duration

    /**
     * 更新进度
     */
    private fun undatePosition() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mAudioPlayerListener!!.onUpdateCurrentPosition(mMediaPlayer!!.currentPosition)
            mHandler?.postDelayed(mUndatePlayPositionRunnable, mSampleTime*1L)
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        mAudioPlayerListener!!.onBufferingUpdate(mp, percent)
    }

    override fun onPrepared(mp: MediaPlayer) {
        state = STATE_PREPARED
        mAudioPlayerListener!!.onPrepared()
    }

    override fun onCompletion(mp: MediaPlayer) {
        state = STATE_STOP
        mAudioPlayerListener!!.onCompletion()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mAudioPlayerListener!!.onError(mp, what, extra)
        return true
    }

    fun reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
        }
    }

    interface AudioPlayerListener {
        /**
         * AudioPlayer准备完成时回调
         */
        fun onPrepared()

        /**
         * AudioPlayer播放完成时回调
         */
        fun onCompletion()

        /**
         * AudioPlayer播放期间每个设置的取样时间间隔回调一次
         *
         * @param position 当前播放位置
         */
        fun onUpdateCurrentPosition(position: Int)

        /**
         * 缓存进度回调
         *
         * @param mp
         * @param percent
         */
        fun onBufferingUpdate(mp: MediaPlayer?, percent: Int)

        /**
         * Called to indicate an error.
         *
         * @param mp
         * @param what
         * @param extra
         */
        fun onError(mp: MediaPlayer?, what: Int, extra: Int)
    }

    companion object {
        /**
         * 状态-未初始化
         */
        const val STATE_UNINITIALIZED = 0

        /**
         * 状态-初始化完毕
         */
        const val STATE_INITIALIZED = 1

        /**
         * 状态-准备完毕
         */
        const val STATE_PREPARED = 2

        /**
         * 状态-播放
         */
        const val STATE_PLAYING = 3

        /**
         * 状态-暂停
         */
        const val STATE_PAUSE = 4

        /**
         * 状态-停止
         */
        const val STATE_STOP = 5
        private var mInstance: MusicPlayer? = null
        val instance: MusicPlayer?
            get() {
                mInstance = MusicPlayer()
                return mInstance
            }
    }
}


