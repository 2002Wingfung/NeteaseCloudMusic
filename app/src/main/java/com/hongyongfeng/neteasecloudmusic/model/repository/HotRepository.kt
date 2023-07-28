package com.hongyongfeng.neteasecloudmusic.model.repository

import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Hot
import com.hongyongfeng.neteasecloudmusic.util.MyApplication

class HotRepository {
    val context=MyApplication.context
    private val hotDao=AppDatabase.getDatabase(context).hotDao()
    fun loadAllHots():List<Hot>{
        return hotDao.loadAllHots()
    }
    fun insert(key:String){
        hotDao.deleteHot(key)
        hotDao.insertHot(Hot(key))
    }
}