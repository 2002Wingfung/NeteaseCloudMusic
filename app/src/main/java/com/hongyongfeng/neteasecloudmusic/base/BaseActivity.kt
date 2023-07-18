package com.hongyongfeng.neteasecloudmusic.base
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.util.KeyboardUtils
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils

abstract class BaseActivity<VB: ViewBinding,VM: ViewModel>(
    private val inflate:(inflater:LayoutInflater)->VB,
    ): AppCompatActivity() {
    lateinit var binding: VB
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            //获取当前获得焦点的View
            val view = currentFocus
            //调用方法判断是否需要隐藏键盘
            KeyboardUtils.hideKeyboard(ev, view, this)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding = inflate(layoutInflater)

        //Log.d("TAG", binding.toString())
        StatusBarUtils.setWindowStatusBarColor(this, R.color.transparent)
        //StatusBarUtils.initStatusView(this)
        //用了上面这行代码会使得底部导航栏变黑
        setContentView(binding.root)
        //initView(StatusBarUtils.getStatusBarHeight(this)+10)
        val window: Window = getWindow()
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}