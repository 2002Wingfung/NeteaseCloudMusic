package com.hongyongfeng.neteasecloudmusic.util

import android.content.Context
import android.widget.Toast
import com.hongyongfeng.neteasecloudmusic.util.MyApplication.Companion.context

fun String.showToast(context:Context,duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, duration).show()
}
fun Int.showToast(context:Context,duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, duration).show()
}
fun String.showToast(duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, duration).show()
}
fun Int.showToast(duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, duration).show()
}