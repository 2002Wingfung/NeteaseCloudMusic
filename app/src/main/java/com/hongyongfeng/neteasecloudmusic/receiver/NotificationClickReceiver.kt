package com.hongyongfeng.neteasecloudmusic.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationClickReceiver : BroadcastReceiver() {

    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //TODO("NotificationClickReceiver.onReceive() is not implemented")
        Log.d("NotificationClickReceiver","通知栏点击");
    }
}