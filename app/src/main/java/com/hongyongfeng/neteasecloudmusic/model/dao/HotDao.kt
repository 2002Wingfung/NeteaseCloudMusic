package com.hongyongfeng.neteasecloudmusic.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hongyongfeng.neteasecloudmusic.model.entity.Hot

@Dao
interface HotDao {
    @Insert
    fun insertHot(hot: Hot):Long

    @Query("select * from Hot order by id desc")
    fun loadAllHots():List<Hot>

    @Query("delete from Hot where searchWord= :searchWord")
    fun deleteHot(searchWord:String)

    @Delete
    fun deleteHot(hot: Hot)//注意更新和删除数据时都是基于User的id值去操作的

    @Query("delete from Song where name= :name")
    fun deleteUserByName(name:String):Int

}