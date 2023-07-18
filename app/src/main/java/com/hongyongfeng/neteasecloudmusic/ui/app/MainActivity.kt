package com.hongyongfeng.neteasecloudmusic.ui.app

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.ui.view.main.MainFragment
import com.hongyongfeng.neteasecloudmusic.ui.view.search.HotFragment


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
    private var mBackPressed: Long = 0

    override fun onBackPressed() {
        supportFragmentManager.fragments[0].apply {
            if (this.childFragmentManager.fragments[0] is MainFragment){
                if (mBackPressed + 500 > System.currentTimeMillis()) {
                    super.onBackPressed()
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "再点击一次返回键退出程序", Toast.LENGTH_SHORT).show()
                    mBackPressed = System.currentTimeMillis()
                }
            }else{
                super.onBackPressed()
                childFragmentManager.fragments[0].apply{
                    if(this.childFragmentManager.fragments.isNotEmpty()){
                        this.childFragmentManager.fragments[0].apply {
                            for (fragment in this.childFragmentManager.fragments){
                                println(fragment)
                                if (fragment is HotFragment) {
                                    try {
                                        activity!!.supportFragmentManager.popBackStack()
                                    }catch (e:Exception){
                                        println(e)
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }

    }
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