package com.hongyongfeng.neteasecloudmusic.testNew

import com.hongyongfeng.neteasecloudmusic.base.BaseViewModel

class TestViewModel : BaseViewModel() {


    /**
     * AUTHOR:hong
     * INTRODUCE:获取需要的Repository
     */
    private val repository by lazy {
        getRepository<TestRepository>()
    }

    fun getData(){
        repository?.getNetWork()
    }
}
