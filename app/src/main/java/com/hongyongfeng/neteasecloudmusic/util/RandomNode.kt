package com.hongyongfeng.neteasecloudmusic.util

import android.content.Context
import android.util.Log
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import java.util.*


class RandomNode {

    companion object{
        private fun judge(arr: IntArray, num: Int): Boolean {
            for (i in arr.indices) {
                if (arr[i] == num) {
                    return false
                }
            }
            return true
        }
        @JvmStatic

        fun randomList(n:Int,context:Context) {
            val arr = IntArray(n) //产生一个能存到n个元素的数组
            for (i in arr.indices) {
                arr[i] = -1 //为了防止默认0被当做检查重复的对象所以全部设为-1
            }
            var index = 0 //index用来做索引值用于数组的迭代
            while (index < arr.size) {
                val rd = Random()
                val num = rd.nextInt(n)+1 //生成1~n之间的随机数
                if (judge(arr, num)) { //judge自定义方法用来判断生成的值是否为数组内重复的元素 如果是 则重新生成
                    arr[index++] = num //如果数组中没有重复的元素就放入数组内 并且索引值++
                }
            }
            val randomDao= AppDatabase.getDatabase(context ).randomDao()
            randomDao.deleteAllRandom()
            randomDao.clearAutoIncrease()
            for (i in arr.indices) {
                println(arr[i])

                randomDao.insert(arr[i])
            }
            randomDao.loadAllRandom().forEach{
                Log.e("random",it.toString()+"id=${it.id}")
            }
        }
    }
}