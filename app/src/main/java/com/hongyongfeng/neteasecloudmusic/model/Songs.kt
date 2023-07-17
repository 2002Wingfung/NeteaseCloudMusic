package com.hongyongfeng.neteasecloudmusic.model


class Songs (
    var id:Int, var name:String,private var artists:List<Artists>?,private var album:Album?,var fee:Int){
    fun getArtists():List<Artists>?=artists
    fun getAlbum():Album?=album
}

class Artists (var id :Int,var name:String)

class Album (var id:Int,var name:String)