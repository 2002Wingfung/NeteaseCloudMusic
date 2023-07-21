package com.hongyongfeng.neteasecloudmusic.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(var firstName:String,var lastName:String,var age:Int)
{
    @PrimaryKey(autoGenerate = true)//主键
    var id:Long=0
}