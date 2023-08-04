package com.hongyongfeng.neteasecloudmusic.model.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.util.MyApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.thread

class RecentlyRepository {
    val context=MyApplication.context
    val songDao=AppDatabase.getDatabase(context).songDao()
    private val randomDao=AppDatabase.getDatabase(context).randomDao()
    private val prefs: SharedPreferences =context.getSharedPreferences("player", Context.MODE_PRIVATE)

    fun getLiveData():LiveData<List<Song>>{
        return songDao.loadFlowAllSongs().asLiveData()
    }
    fun getFlow(): Flow<List<Song>> =songDao.loadFlowAllSongs()
    fun getSongList(listener:(list:List<Song>)->Unit){
        val job = Job()

        val scope = CoroutineScope(job)

        scope.launch {
            // 处理具体的逻辑
            withContext(Dispatchers.IO){
                //listener(songDao.loadAllSongs())
                //println("success")
                songDao.loadFlowAllSongs().onEach {
                    //println(it)
                    listener(it)
                    job.cancel()
                }.launchIn(this)
            }
        }

        //thread {
//            listener(songDao.loadAllSongs())
        //}
    }
    fun updateSong(song:Song){
        songDao.updateSong(song)
    }
    fun updateSongById(id:Long){
        songDao.updateIsPlaying(false, lastPlay = true)
        songDao.updateLastPlaying(false, origin = true)
        songDao.updateLastPlayingById(true,id)
        songDao.updateIsPlaying(true, lastPlay = true)
    }
    fun deleteSongById(position:Int){
        songDao.deleteSongById(position+1)
        songDao.updateSongById(position+2)
    }
    fun loadRandomBySongId(id:Int): Random {
        return randomDao.loadRandomBySongId(id)
    }
    fun selectMaxId():Long{
        return songDao.selectMaxId()
    }
    fun getPlayMode():Int{
        return prefs.getInt("mode",-1)
    }
}