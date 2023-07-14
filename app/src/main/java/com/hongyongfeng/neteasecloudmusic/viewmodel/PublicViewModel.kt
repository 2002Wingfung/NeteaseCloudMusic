package com.hongyongfeng.neteasecloudmusic.viewmodel

import androidx.lifecycle.ViewModel

/**
 * 多个Fragment都可以请求网络，可能有相同的代码块，比如request等
 * 都可以集成在PublicViewModel这个类里面，避免重复写同样的代码
 */

class PublicViewModel :ViewModel(){
}