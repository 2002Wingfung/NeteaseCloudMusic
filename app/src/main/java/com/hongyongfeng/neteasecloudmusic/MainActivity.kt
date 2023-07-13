package com.hongyongfeng.neteasecloudmusic

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.util.showToast

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                R.id.item2-> {
                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()

                }

                R.id.item3-> {
                    Toast.makeText(this, "深色模式", Toast.LENGTH_SHORT).show()

                }
                R.id.item4->{
                    Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();

                }
                R.id.item5-> {
                    Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()

                }
            }
            false
        }

    }

    private fun initListener() {
        binding.btnNva.setOnClickListener{
            binding.drawerLayout.openDrawer(GravityCompat.START)
            println("success")
        }
    }


}