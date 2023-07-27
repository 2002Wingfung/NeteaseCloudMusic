package com.hongyongfeng.neteasecloudmusic.base

abstract class BaseModel <out VM :BaseViewModel<BaseModel<VM,*>,*>,CONTRACT>(mViewModel: VM): Base<CONTRACT>() {
    var mViewModel: @UnsafeVariance VM
    init {
        this.mViewModel=mViewModel
    }
}

