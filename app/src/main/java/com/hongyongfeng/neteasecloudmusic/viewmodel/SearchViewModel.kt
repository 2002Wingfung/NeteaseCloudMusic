package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.model.repository.SearchRepository
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.RequestBuilder
import com.hongyongfeng.neteasecloudmusic.testNew.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import java.text.FieldPosition

class SearchViewModel :BaseViewModel(){
    val testValue by lazy {
        MutableLiveData("")
    }
    /**
     * AUTHOR:hong
     * INTRODUCE:获取需要的Repository
     */
    private val repository by lazy {
        getRepository<SearchRepository>()
    }
    fun clearTableSong(){
        repository?.deleteTableSong()
    }
    fun insertSong(song:Song):Long{
        return repository?.insertSong(song)?:1
    }
    fun updateSong(song:Song){
        repository?.updateSong(song)
    }
    fun loadLastPlayingSong():Song?{
        return repository?.loadLastPlayingSong()
    }
    fun selectMaxId():Long{
        return repository?.selectMaxId()?:0
    }
    fun plusSongById(mId:Long){
        repository?.plusSongById(mId)
    }
    fun getPlayMode():Int{
        return repository?.getPlayMode()?:-1
    }
    fun loadRandomBySongId(id: Int): Random? {
        return repository?.loadRandomBySongId(id)
    }
}