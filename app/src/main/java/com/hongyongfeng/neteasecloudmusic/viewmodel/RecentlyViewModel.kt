package com.hongyongfeng.neteasecloudmusic.viewmodel

import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.model.repository.RecentlyRepository

class RecentlyViewModel: BaseViewModel() {
    fun getSongList(listener:(list:List<Song>)->Unit){
        repository?.getSongList(listener)
    }
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