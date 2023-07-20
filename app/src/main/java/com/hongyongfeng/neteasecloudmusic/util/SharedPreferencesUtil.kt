package com.hongyongfeng.neteasecloudmusic.util

import android.content.SharedPreferences

class SharedPreferencesUtil {
    fun SharedPreferences.open(block:SharedPreferences.Editor.()->Unit){
        val editor=edit()
        editor.block()
        editor.apply()
    }
}