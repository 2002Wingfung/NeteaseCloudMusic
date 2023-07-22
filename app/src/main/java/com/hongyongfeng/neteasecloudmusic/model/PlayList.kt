package com.hongyongfeng.neteasecloudmusic.model

import android.os.Parcelable.ClassLoaderCreator

class PlayList (var creator:Creator,var userId:Long,var trackCount :Long, var coverImgUrl:String,var ordered:Boolean,var name:String,var id:Long)

class Creator(var nickname:String)