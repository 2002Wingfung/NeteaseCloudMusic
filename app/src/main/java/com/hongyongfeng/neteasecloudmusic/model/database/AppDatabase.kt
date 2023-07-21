package com.hongyongfeng.neteasecloudmusic.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hongyongfeng.neteasecloudmusic.model.dao.UserDao
import com.hongyongfeng.neteasecloudmusic.model.entity.User

@Database(version = 1, entities = [User::class])
abstract class AppDatabase :RoomDatabase(){
    abstract fun userDao():UserDao
    companion object{
        private var instance:AppDatabase?=null

        @Synchronized
        fun getDatabase(context:Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,"app_database")
                //.allowMainThreadQueries()
                .build().apply {
                    instance=this
                }
        }
    }
}