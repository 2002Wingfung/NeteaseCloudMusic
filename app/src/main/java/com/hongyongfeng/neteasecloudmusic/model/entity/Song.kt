package com.hongyongfeng.neteasecloudmusic.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(var name:String,var songId:Long,var albumId:Long,var artist:String,var isPlaying:Boolean=false,var lastPlaying:Boolean=false,val albumUrl:String?=null)
{
    @PrimaryKey(autoGenerate = true)//主键
    var id:Long=0
}