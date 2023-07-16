package com.hongyongfeng.neteasecloudmusic.model


class Songs (
    private var id:Int, private var name:String,private var artists:List<Artists>?,private var album:Album?){
    fun getName()=name
    fun getId()=id

}

class Artists {

}

class Album {

}
