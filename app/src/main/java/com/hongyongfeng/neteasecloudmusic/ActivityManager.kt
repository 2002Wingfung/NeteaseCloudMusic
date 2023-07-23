package com.hongyongfeng.neteasecloudmusic

import android.app.Activity
import java.lang.ref.WeakReference


class ActivityManager {


    companion object{
        /**
         * 弱引用
         */
        private var activityWeakReference: WeakReference<Activity>? = null

        private val activityUpdateLock = Any()
        /**
         * 得到当前Activity
         * @return
         */
        fun getCurrentActivity(): Activity? {
            var currentActivity: Activity? = null
            synchronized(activityUpdateLock) {
                if (activityWeakReference != null) {
                    currentActivity = activityWeakReference!!.get()
                }
            }
            return currentActivity
        }

        /**
         * 设置当前Activity
         * @return
         */
        fun setCurrentActivity(activity: Activity?) {
            synchronized(activityUpdateLock) {
                activityWeakReference = WeakReference<Activity>(activity)
            }
        }
    }

}