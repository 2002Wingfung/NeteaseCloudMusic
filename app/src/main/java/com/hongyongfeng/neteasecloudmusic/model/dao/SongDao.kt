package com.hongyongfeng.neteasecloudmusic.model.dao

import androidx.room.*
import com.hongyongfeng.neteasecloudmusic.model.entity.Song

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song:Song):Long



    @Update
    fun updateSong(newSong:Song)//注意更新和删除数据时都是基于User的id值去操作的

    @Query("select * from Song ")
    fun loadAllSongs():List<Song>

    @Query("select id from Song where lastPlaying=1")
    fun loadId():Int
    @Query("select * from Song where isPlaying==1")
    fun loadIsPlayingSong():Song?

    @Query("update song set isPlaying = :newStatus where lastPlaying== :lastPlay")
    fun updateIsPlaying(newStatus:Boolean, lastPlay:Boolean)

    @Query("update song set lastPlaying= :lastPlaying  where lastPlaying=:origin")
    fun updateLastPlaying(lastPlaying:Boolean,origin:Boolean)
    @Query("update song set lastPlaying= :lastPlaying where id=:id")
    fun updateLastPlayingById(lastPlaying:Boolean,id:Long)
    @Query("select * from Song where lastPlaying==1")
    fun loadLastPlayingSong():Song?
    @Query("update song set albumUrl = :albumUrl  where albumId= :albumId")
    fun updateAlbumUrl(albumUrl:String,albumId:Int)
    @Query("update song set albumUrl = :albumUrl ,lastPlaying=1 where albumId= :albumId")
    fun updateAlbumUrlAndLastPlaying(albumUrl:String,albumId:Int)
    @Query("delete FROM sqlite_sequence WHERE name = 'Song'")
    fun clearAutoIncrease()

    @Query("select * from Song where id > :id")
    fun loadUsersOlderThan(id:Int):List<Song>

    @Delete
    fun deleteSong(song:Song)//注意更新和删除数据时都是基于User的id值去操作的


    @Query("delete from Song ")
    fun deleteAllSong()


    @Query("delete from Song where id= :id")
    fun deleteSongById(id:Int)
    @Query("update Song set id = id-1 where id >= :id")
    fun updateSongById(id:Int)

    @Query("update Song set id = id+1 where id == :id")
    fun plusSongById(id:Long)
    @Query("select max(id) from Song")
    fun selectMaxId():Long
}