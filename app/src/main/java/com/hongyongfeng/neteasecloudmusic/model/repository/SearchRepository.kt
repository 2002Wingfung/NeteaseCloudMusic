package com.hongyongfeng.neteasecloudmusic.model.repository

import android.content.Context
import android.content.SharedPreferences
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.util.MyApplication

class SearchRepository {
    val context=MyApplication.context
    val songDao=AppDatabase.getDatabase(context).songDao()
    private val randomDao=AppDatabase.getDatabase(context).randomDao()

    private val prefs: SharedPreferences =context.getSharedPreferences("player", Context.MODE_PRIVATE)

    fun deleteTableSong(){
        songDao.deleteAllSong()
        songDao.clearAutoIncrease()
    }
    fun insertSong(song: Song):Long{
        return songDao.insertSong(song)
    }
    fun updateSong(song:Song){
        songDao.updateSong(song)
    }
    fun loadLastPlayingSong():Song?{
        return songDao.loadLastPlayingSong()
    }
    fun selectMaxId():Long{
        return songDao.selectMaxId()
    }
    fun plusSongById(mId:Long){
        songDao.plusSongById(mId)
    }
    fun getPlayMode():Int{
        return prefs.getInt("mode",-1)
    }
    fun loadRandomBySongId(id:Int):Random{
        return randomDao.loadRandomBySongId(id)
    }
}