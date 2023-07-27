package com.hongyongfeng.neteasecloudmusic.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hongyongfeng.neteasecloudmusic.model.entity.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user:User):Long
    @Update
    fun updateUser(newUser:User)//注意更新和删除数据时都是基于User的id值去操作的
    @Query("select * from user")
    fun loadAllUsers():List<User>
    @Query("select * from User where age > :age")
    fun loadUsersOlderThan(age:Int):List<User>
    @Delete
    fun deleteUser(user:User)//注意更新和删除数据时都是基于User的id值去操作的
    @Query("delete from User where lastName= :lastName")
    fun deleteUserByLastName(lastName:String):Int
}