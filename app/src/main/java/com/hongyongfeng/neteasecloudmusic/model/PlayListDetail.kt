package com.hongyongfeng.neteasecloudmusic.model

class PlayListDetail(var songs:List<Detail>)
class Detail(var name:String,var id:Long,var ar :List<Artist>,var al:Al)
class Artist(var name:String)
class Al(var id:Long,var name:String,var picUrl:String)