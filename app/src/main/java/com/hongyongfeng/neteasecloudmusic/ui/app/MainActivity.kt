package com.hongyongfeng.neteasecloudmusic.ui.app

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayListInterface
import com.hongyongfeng.neteasecloudmusic.network.api.SearchInterface
import com.hongyongfeng.neteasecloudmusic.service.MusicService
import com.hongyongfeng.neteasecloudmusic.ui.view.main.MainFragment
import com.hongyongfeng.neteasecloudmusic.ui.view.search.HotFragment
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


class MainActivity : BaseActivity<ActivityMainBinding,ViewModel>(ActivityMainBinding::inflate,true) {
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
    /**
     * 图片动画
     */
    private lateinit var logoAnimation: ObjectAnimator
    var mBackPressed: Long = 0
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
        }
        override fun onServiceDisconnected(name: ComponentName) {}
    }
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

                childFragmentManager.fragments[0].apply{
                    if(this.childFragmentManager.fragments.isNotEmpty()){
                        this.childFragmentManager.fragments[0].apply {
                            for (fragment in this.childFragmentManager.fragments){
                                //println(fragment)
                                //Log.d("fragmentback",fragment.toString())
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
                super.onBackPressed()
            }
        }

    }
    private val mHandler: Handler = object :Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 展示给进度条和当前时间


            //更新进度
            updateProgress()
        }
    }
    /**
     * 更新进度
     */
    private fun updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        //mHandler.sendEmptyMessageDelayed(0, 1000)
    }

    /**
     * 初始化动画
     */
    private fun initAnimation() {
        logoAnimation = ObjectAnimator.ofFloat(binding.ivLogo, "rotation", 0.0f, 360.0f)
        logoAnimation.duration = 6000
        logoAnimation.interpolator = LinearInterpolator()
        logoAnimation.repeatCount = -1
        logoAnimation.repeatMode = ObjectAnimator.RESTART
    }
    @SuppressLint("SetTextI18n")
    private fun initBottomPlayer(){
        val songDao=AppDatabase.getDatabase(this).songDao()
        thread {
            val song=songDao.loadIsPlayingSong()
            if (song!=null){

                runOnUiThread{
                    //这个不用改
                    logoAnimation.start()
                    binding.btnPlay.setIconResource(R.drawable.ic_pause)
                }
            }
            val lastSong=songDao.loadLastPlayingSong()
            if (lastSong!=null){
                val text=lastSong.name+" - "+lastSong.artist
                binding.tvSongName.text=text
                var url=lastSong.albumUrl
                if (url.isNullOrEmpty()){
                    //请求网络获取专辑图片url
                    //这个要改
                    url="网络请求获得的url"
                }
                runOnUiThread{
                    //到时候要把加载图片的代码放到判断是否最后一首歌的方法中
                    Picasso.get().load(url).fit().into(binding.ivLogo)
                }

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nav: NavigationView =binding.navView
        nav.layoutParams.width=getResources().getDisplayMetrics().widthPixels *4/ 5;//屏幕的三分之一
        nav.setLayoutParams(nav.layoutParams);
        val headerLayout =nav.inflateHeaderView(R.layout.nav_header)
        headerLayout.setOnClickListener{
            findNavController(R.id.app_nav).navigate(R.id.action_mainFragment_to_loginFragment)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 ->{
                    Toast.makeText(this, "我的消息", Toast.LENGTH_SHORT).show();
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
        binding.layBottom.setOnClickListener {

            thread {
                val songDao=AppDatabase.getDatabase(this).songDao()
                val lastSong=songDao.loadLastPlayingSong()

                val bundle=Bundle()
                bundle.putString("name",lastSong?.name)
                lastSong?.songId?.toInt()?.let { it1 -> bundle.putInt("id", it1) }
                lastSong?.albumId?.toInt()?.let { it1 -> bundle.putInt("albumId", it1) }
                bundle.putString("singer",lastSong?.artist)
                bundle.putInt("status",-1)
                val intent=Intent(this, PlayerActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

        }
        initAnimation()
        initBottomPlayer()
        //updateProgress()
        //StatusBarUtils.setWindowStatusBarColor(this, R.color.transparent)
//        setContentView(binding.root)
////        val window: Window = getWindow()
////        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN )


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

        val intent = Intent(this@MainActivity, MusicService::class.java)


        bindService(intent,mServiceConnection,BIND_AUTO_CREATE)

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


    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, MusicService::class.java)
        stopService(intent)

        unbindService(mServiceConnection)
    }

    fun onClick(view: View) {
        "播放".showToast(this)

        val songDao=AppDatabase.getDatabase(this).songDao()
        thread {
            for (song in songDao.loadAllSongs()){
                Log.e("MainActivity",song.toString()+"id:${song.id}")
            }
        }
    }


}