package com.hongyongfeng.neteasecloudmusic.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * liveData管道总线
 *
 * @author Wingfung Hung
 */
class LiveDataBus private constructor() {
    /**
     * LiveData集合
     */
    private val liveDataMap: MutableMap<String, BusMutableLiveData<Any>>

    init {
        liveDataMap = HashMap()
    }

    /**
     * 这个是存和取一体的方法
     * @param key
     * @param clazz
     * @param <T>
     * @return
    </T> */
    @Synchronized
    fun <T> with(key: String, clazz: Class<T>?): BusMutableLiveData<T> {
        if (!liveDataMap.containsKey(key)) {
            liveDataMap[key] = BusMutableLiveData()
        }
        return liveDataMap[key] as BusMutableLiveData<T>
    }

    class BusMutableLiveData<T> : MutableLiveData<T>() {
        //是否需要粘性事件
        private var isHad = false

        //重写observe的方法
        fun observe(owner: LifecycleOwner, isHad: Boolean, observer: Observer<in T>) {
            this.isHad = isHad
            this.observe(owner, observer)
        }

        //重写observe的方法
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            //改变observer.mLastVersion >= mVersion这个判断  然后拦截onChanged
            try {
                if (isHad) {
                    hook(observer as Observer<T>)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * hook方法  hook系统源码  改变系统的一些参数
         * @param observer
         */
        @Throws(Exception::class)
        private fun hook(observer: Observer<T>) {
            //获取到LiveData的类对象
            val liveDataClass: Class<LiveData<*>> = LiveData::class.java
            //获取到mObservers的反射对象
            val mObserversField: Field = liveDataClass.getDeclaredField("mObservers")
            //让mObserversField可以被访问
            mObserversField.isAccessible = true
            //获取到这个mObserversField的值
            val mObservers: Any = mObserversField.get(this) as Any
            //获取到mObservers的get方法的反射对象
            val get: Method = mObservers.javaClass.getDeclaredMethod("get", Any::class.java)
            //设置这个反射对象可以被访问
            get.isAccessible = true
            //执行这个方法 得到Entry
            val invokeEntry: Any? = get.invoke(mObservers, observer)
            //定义一个空的对象  LifecycleBoundObserver
            var observerWrapper: Any? = null
            if (invokeEntry != null && invokeEntry is Map.Entry<*, *>) {
                observerWrapper = invokeEntry.value
            }
            if (observerWrapper == null) {
                throw NullPointerException("ObserverWrapper不能为空")
            }
            //获取到ObserverWrapper的类对象
            val superclass: Class<*> = observerWrapper.javaClass.superclass as Class<*>
            //获取搭配这个类中的mLastVersion成员变量
            val mLastVersionField: Field = superclass.getDeclaredField("mLastVersion")
            mLastVersionField.isAccessible = true
            //获取到mVersion的反射对象
            val mVersionField: Field = liveDataClass.getDeclaredField("mVersion")
            //打开权限
            mVersionField.isAccessible = true
            //得到的就是mVersion在当前类中的值
            val o: Any = mVersionField.get(this) as Any
            //把它的值给mLastVersion
            mLastVersionField.set(observerWrapper, o)
        }
    }

    companion object {
        val instance = LiveDataBus()
    }
}