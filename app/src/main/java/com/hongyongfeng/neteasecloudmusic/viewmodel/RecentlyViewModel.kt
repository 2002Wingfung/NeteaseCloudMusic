package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.util.MyApplication
import kotlin.concurrent.thread

class RecentlyViewModel: ViewModel() {
    var songList= MutableLiveData<List<Song>>()
    private val songDao= AppDatabase.getDatabase(MyApplication.context).songDao()

    fun setSongList(){
        thread {
            songList.postValue(songDao.loadAllSongs())
        }
    }
    fun getSongList(listener:(list:List<Song>)->Unit){
        thread {
            listener(songDao.loadAllSongs())
        }
    }


}