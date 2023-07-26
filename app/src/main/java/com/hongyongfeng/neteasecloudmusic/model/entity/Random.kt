package com.hongyongfeng.neteasecloudmusic.model.entity

import android.graphics.Bitmap
import androidx.room.*


@Entity(foreignKeys = [ForeignKey(entity =Song::class,parentColumns= arrayOf("id"), childColumns =arrayOf("song_id"))])
data class Random(@ColumnInfo(name = "song_id") var songId:Long)
{
    @PrimaryKey(autoGenerate = true)//主键
    var id:Long=0
}