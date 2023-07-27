package com.hongyongfeng.neteasecloudmusic.model.dao

import androidx.room.*
import com.hongyongfeng.neteasecloudmusic.model.entity.Random
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.model.entity.User

@Dao
interface RandomDao {
    @Query("select* from random")
    fun loadAllRandom():List<Random>
    @Query("select * from random where song_id=:id")
    fun loadRandomBySongId(id:Int):Random
    @Query("SELECT r.id id,s.name name,s.songId songId,s.albumId albumId,s.artist artist,s.isPlaying isPlaying,s.lastPlaying lastPlaying,s.albumUrl albumUrl FROM Random r,Song s where r.song_id==s.id ORDER BY r.id ASC")
    fun loadAllRandomSong():List<Song>

    @Query("select r.id from Random r,Song s where r.song_id==s.id and lastPlaying==1 ORDER BY r.id ASC ")
    fun loadLastPlayingSongId():Int?
    @Query("insert into random (song_id) values (:id)")
    fun insert(id:Int)

    @Query("delete FROM sqlite_sequence WHERE name = 'Random'")
    fun clearAutoIncrease()
    @Query("delete from Random ")
    fun deleteAllRandom()
}