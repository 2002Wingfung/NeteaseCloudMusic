package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
//import androidx.lifecycle.Transformations
import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.model.repository.RecentlyRepository
import kotlinx.coroutines.flow.Flow

class RecentlyViewModel: BaseViewModel() {
    fun getSongList(listener:(list:List<Song>)->Unit){
        val viewModelListener:(list:List<Song>)->Unit={
            listener(it)
        }
        repository?.getSongList(viewModelListener)
    }
    private val songLiveData= MutableLiveData<Any?>()
    val result=songLiveData.switchMap{
        repository!!.getLiveData()
    }
    fun getLiveData(){
        songLiveData.value=songLiveData.value
    }
    fun getFlow(): Flow<List<Song>> =repository!!.getFlow()
    /**
     * AUTHOR:hong
     * INTRODUCE:获取需要的Repository
     */
    private val repository by lazy {
        getRepository<RecentlyRepository>()
    }
    fun getPlayMode():Int{
        return repository?.getPlayMode()?:-1
    }
    fun selectMaxId():Long{
        return repository?.selectMaxId()?:0
    }
    fun loadRandomBySongId(id: Int): Random? {
        return repository?.loadRandomBySongId(id)
    }
    fun updateSong(song:Song){
        repository?.updateSong(song)
    }
    fun updateSongById(id:Long){
        repository?.updateSongById(id)
    }
    fun deleteSongById(position:Int){
        repository?.deleteSongById(position)
    }
}