package com.hongyongfeng.neteasecloudmusic.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.model.entity.User

@Dao
interface RandomDao {
    @Query("select* from random")
    fun loadAllRandom():List<Random>

    @Query("insert into random (song_id) values (:id)")
    fun insert(id:Int)

    @Query("delete FROM sqlite_sequence WHERE name = 'Random'")
    fun clearAutoIncrease()
    @Query("delete from Random ")
    fun deleteAllRandom()
}