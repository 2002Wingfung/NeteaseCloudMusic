package com.hongyongfeng.neteasecloudmusic.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Hot
import com.hongyongfeng.neteasecloudmusic.model.dao.HotDao
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.entity.Song

@Database(version = 1, entities = [Song::class, Hot::class])
abstract class AppDatabase :RoomDatabase(){
    abstract fun songDao():SongDao
    abstract fun hotDao(): HotDao
    companion object{
        private var instance:AppDatabase?=null

        @Synchronized
        fun getDatabase(context:Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,"app_database.db")
                //.fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build().apply {
                    instance=this
                }
        }
    }
}