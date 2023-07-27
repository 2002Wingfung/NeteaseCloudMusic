package com.hongyongfeng.neteasecloudmusic.receiver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hongyongfeng.neteasecloudmusic.ActivityManager

class NotificationClickReceiver : BroadcastReceiver() {

    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        //获取栈顶的Activity
        val currentActivity: Activity = ActivityManager.getCurrentActivity()!!
        val intent1 = Intent(Intent.ACTION_MAIN)
        intent1.addCategory(Intent.CATEGORY_LAUNCHER)
        intent1.setClass(context, currentActivity.javaClass)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//        val intent2=Intent(context.applicationContext,MainActivity::class.java)
//        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        //context.applicationContext.startActivity(intent1)
        //println(context)
        try {
            context.startActivity(intent1)
        }catch (e:Exception){
            e.printStackTrace()
        }
        Log.d("NotificationClickReceiver","通知栏点击");
        //这样就可以实现，点击通知栏时跳转到栈顶的Activity而不是新建一个Activity。
    }
}