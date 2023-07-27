package com.hongyongfeng.neteasecloudmusic.model.entity

import androidx.room.*

@Entity
data class Random(@ColumnInfo(name = "song_id") var songId:Long)
{
    @PrimaryKey(autoGenerate = true)//主键
    var id:Long=0
}