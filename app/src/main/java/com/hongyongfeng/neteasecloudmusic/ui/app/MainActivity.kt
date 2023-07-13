package com.hongyongfeng.neteasecloudmusic.ui.app

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        StatusBarUtils.setWindowStatusBarColor(this, R.color.transparent)
        StatusBarUtils.initStatusView(this)
        setContentView(binding.root)
        initView(StatusBarUtils.getStatusBarHeight(this)+10)

        val window: Window = getWindow()
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN )

        initListener()
        val nav:NavigationView=binding.navView
        nav.layoutParams.width=getResources().getDisplayMetrics().widthPixels *3/ 4;//屏幕的三分之一

        nav.setLayoutParams(nav.layoutParams);
        val headerLayout =nav.inflateHeaderView(R.layout.nav_header)
        headerLayout.setOnClickListener{
            "登录".showToast(this)
        }
        nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 ->{
                    Toast.makeText(this, "我的消息", Toast.LENGTH_SHORT).show();
                    //加载碎片
                    //getSupportFragmentManager().beginTransaction().replace(R.id.content,new Fragment_05()).commit();
                    //binding.drawerLayout.closeDrawer(GravityCompat.START);//关闭侧滑栏

                }
                R.id.item2 -> {
                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()

                }

                R.id.item3 -> {
                    Toast.makeText(this, "深色模式", Toast.LENGTH_SHORT).show()

                }
                R.id.item4 ->{
                    Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();

                }
                R.id.item5 -> {
                    Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()

                }
            }
            false
        }

    }

    private fun initView(dp:Int){
        val lp = binding.actionBar.getLayoutParams() as LinearLayout.LayoutParams
        lp.topMargin= dp
        binding.actionBar.setLayoutParams(lp)
    }
    private fun initListener() {
        binding.btnNva.setOnClickListener{
            binding.drawerLayout.openDrawer(GravityCompat.START)
            println("success")
        }
        binding.btnQrcode.setOnClickListener{
            "扫描二维码".showToast(this)
        }
        binding.searchBar.setOnClickListener{
            "查找歌曲".showToast(this)
        }
    }


}