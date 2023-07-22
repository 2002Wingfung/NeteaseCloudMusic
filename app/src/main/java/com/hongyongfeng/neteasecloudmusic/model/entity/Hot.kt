package com.hongyongfeng.neteasecloudmusic.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Hot(
    var searchWord:String
){
    @PrimaryKey(autoGenerate = true)//主键
    var id:Long=0
}