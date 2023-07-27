package com.hongyongfeng.neteasecloudmusic.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


open class BaseViewModel : ViewModel() {
    /**
     * 控制状态视图的LiveData
     */
    val mStateViewLiveData = MutableLiveData<Int>()

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取Repository
     */
    inline fun <reified R> getRepository(): R? {
        try {
            val clazz = R::class.java
            return clazz.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
