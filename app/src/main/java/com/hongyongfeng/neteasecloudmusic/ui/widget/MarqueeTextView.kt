package com.hongyongfeng.neteasecloudmusic.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
@SuppressLint("AppCompatCustomView")
class MarqueeTextView: TextView {
    constructor(context: Context):super(context)

    constructor(context: Context?, attrs: AttributeSet?) :super(context, attrs)


    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :super(context, attrs, defStyle)

    override fun isFocused(): Boolean {
        return true
    }

}