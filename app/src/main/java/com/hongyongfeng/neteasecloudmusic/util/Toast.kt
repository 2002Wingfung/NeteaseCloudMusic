package com.hongyongfeng.neteasecloudmusic.util

import android.content.Context
import android.widget.Toast

fun String.showToast(context:Context,duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}
fun Int.showToast(context:Context,duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}