package com.hongyongfeng.neteasecloudmusic.model

import android.icu.text.CaseMap.Title

class PlayListBean(private var title:String="",
        private var amount:Int=0) {


    fun getTitle()=title
    fun getAmount()=amount
    fun setTitle(title: String){
        this.title=title
    }
    fun setAmount(amount:Int){
        this.amount=amount
    }
}