package com.hongyongfeng.neteasecloudmusic.ui.app

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.util.KeyboardUtils
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils


class MainActivity : BaseActivity<ActivityMainBinding,ViewModel>(ActivityMainBinding::inflate) {
    //private lateinit var binding:ActivityMainBinding
//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        if (ev.action == MotionEvent.ACTION_DOWN) {
//            //获取当前获得焦点的View
//            val view = currentFocus
//            //调用方法判断是否需要隐藏键盘
//            KeyboardUtils.hideKeyboard(ev, view, this)
//        }
//        return super.dispatchTouchEvent(ev)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //StatusBarUtils.setWindowStatusBarColor(this, R.color.transparent)
//        setContentView(binding.root)
//        val window: Window = getWindow()
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN )


        //initListener()
//        val nav:NavigationView=binding.navView
//        nav.layoutParams.width=getResources().getDisplayMetrics().widthPixels *4/ 5;//屏幕的三分之一
//
//        nav.setLayoutParams(nav.layoutParams);
//        val headerLayout =nav.inflateHeaderView(R.layout.nav_header)
//        headerLayout.setOnClickListener{
//            "登录".showToast(this)
//        }
//        nav.setNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.item1 ->{
//                    Toast.makeText(this, "我的消息", Toast.LENGTH_SHORT).show();
//                    //加载碎片
//                    //getSupportFragmentManager().beginTransaction().replace(R.id.content,new Fragment_05()).commit();
//                    //binding.drawerLayout.closeDrawer(GravityCompat.START);//关闭侧滑栏
//
//                }
//                R.id.item2 -> {
//                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
//
//                }
//
//                R.id.item3 -> {
//                    Toast.makeText(this, "深色模式", Toast.LENGTH_SHORT).show()
//
//                }
//                R.id.item4 ->{
//                    Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
//
//                }
//                R.id.item5 -> {
//                    Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//            false
//        }

    }

//    private fun initView(dp:Int){
//        val lp = binding.actionBar.getLayoutParams() as LinearLayout.LayoutParams
//        lp.topMargin= dp
//        binding.actionBar.setLayoutParams(lp)
//    }
//    private fun initListener() {
//        binding.btnNva.setOnClickListener{
//            binding.drawerLayout.openDrawer(GravityCompat.START)
//            println("success")
//        }
//        binding.btnQrcode.setOnClickListener{
//            "扫描二维码".showToast(this)
//        }
//        binding.searchBar.setOnClickListener{
//            "查找歌曲".showToast(this)
//        }
//    }


}