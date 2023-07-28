package com.hongyongfeng.neteasecloudmusic.ui.app

import com.hongyongfeng.neteasecloudmusic.livedata.LiveDataBus
import com.hongyongfeng.neteasecloudmusic.livedata.LiveDataBus.BusMutableLiveData
import android.animation.ObjectAnimator
import android.content.*
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.hongyongfeng.neteasecloudmusic.*
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityMainBinding
import com.hongyongfeng.neteasecloudmusic.model.dao.RandomDao
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.service.MusicService
import com.hongyongfeng.neteasecloudmusic.ui.view.main.MainFragment
import com.hongyongfeng.neteasecloudmusic.ui.view.search.HotFragment
import com.permissionx.guolindev.PermissionX
import com.squareup.picasso.Picasso
import kotlin.concurrent.thread

class MainActivity : BaseActivity<ActivityMainBinding,ViewModel>(ActivityMainBinding::inflate,true) {
    /**
     * 图片动画
     */
    private lateinit var logoAnimation: ObjectAnimator
    private var mBackPressed: Long = 0
    private lateinit var prefs: SharedPreferences
    private var counts=0
    private lateinit var imgButton: MaterialButton
    private var musicBinder:MusicService.MediaPlayerBinder?=null
    private var musicService: MusicService? = null
    private lateinit var songDao:SongDao
    private lateinit var randomDao:RandomDao
    private val requestList = ArrayList<String>()
    private lateinit var headerLayout:View
    private lateinit var nav:NavigationView
    /**
     * 当Service中通知栏有变化时接收到消息
     */
    private var activityLiveData: BusMutableLiveData<String>? = null
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicBinder = service as MusicService.MediaPlayerBinder
            musicService = musicBinder!!.getMusicService()
            Log.d("Binder", "Service与Activity已连接")
        }
        override fun onServiceDisconnected(name: ComponentName) {
            musicBinder=null
        }
    }
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
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
    private fun initBottomPlayer(){
        val songDao=AppDatabase.getDatabase(this).songDao()
        thread {
            val song=songDao.loadIsPlayingSong()
            if (song!=null){
                runOnUiThread{
                    //这个不用改
                    logoAnimation.start()
                    imgButton.setIconResource(R.drawable.ic_pause)
                }
            }
            val lastSong=songDao.loadLastPlayingSong()
            if (lastSong!=null){
                if (lastSong.name.isEmpty()||lastSong.artist.isEmpty()){
                    binding.tvSongName.text="欢迎使用音乐播放器"
                }else{
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
    }

    /**
     * 通知栏动作观察者
     */
    private fun notificationObserver() {
        songDao=AppDatabase.getDatabase(this).songDao()
        randomDao=AppDatabase.getDatabase(this).randomDao()
        activityLiveData = LiveDataBus.instance.with("activity_control", String::class.java)
        activityLiveData!!.observe(this@MainActivity, true
        ) { value ->
            when (value) {
                PLAY -> {
                    imgButton.setIconResource(R.drawable.ic_pause)
                    if (logoAnimation.isPaused) {
                        logoAnimation.resume()
                    }else{
                        logoAnimation.start()
                    }
                }
                PAUSE, CLOSE -> {
                    logoAnimation.pause()
                    imgButton.setIconResource(R.drawable.ic_play_circle_2)
                }
                PREV -> {
                    Log.d(TAG, "上一曲")
                    runOnUiThread{
                        val song=songDao.loadLastPlayingSong()
                        val text=song?.name+" - "+song?.artist
                        binding.tvSongName.text=text
                        Picasso.get().load(song?.albumUrl).fit().into(binding.ivLogo)
                    }
                }
                NEXT -> {
                    Log.d(TAG, "下一曲")
                    runOnUiThread{
                        val song=songDao.loadLastPlayingSong()
                        val text=song?.name+" - "+song?.artist
                        binding.tvSongName.text=text
                        Picasso.get().load(song?.albumUrl).fit().into(binding.ivLogo)
                    }
                }
                else -> {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取通知权限
        requestList.add(PermissionX.permission.POST_NOTIFICATIONS)
        PermissionX.init(this)
            .permissions(requestList)
            .onExplainRequestReason { scope, deniedList ->
                val message = "PermissionX需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    //Toast.makeText(this, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("false",deniedList.toString())
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
        nav=binding.navView
        nav.layoutParams.width= resources.displayMetrics.widthPixels *4/ 5//屏幕的三分之一
        nav.layoutParams = nav.layoutParams
        headerLayout =nav.inflateHeaderView(R.layout.nav_header)
        prefs=getSharedPreferences("player", Context.MODE_PRIVATE)
        initView()
        initListener()
        initAnimation()
        initBottomPlayer()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE)
        notificationObserver()
    }

    override fun onStart() {
        super.onStart()
        Log.e("Main","start")
        val song=songDao.loadLastPlayingSong()
        if (song != null) {
            if (song.name.isEmpty()||song.artist.isEmpty()){
                binding.tvSongName.text="欢迎使用音乐播放器"
            }else{
                val text= song.name +" - "+ song.artist
                binding.tvSongName.text=text
                Picasso.get().load(song.albumUrl).fit().into(binding.ivLogo)
            }
        }
        val isPlayingSong=songDao.loadIsPlayingSong()
        if (isPlayingSong!=null){
            if (logoAnimation.isPaused){
                logoAnimation.resume()
            }else{
                logoAnimation.start()
            }
            imgButton.setIconResource(R.drawable.ic_pause)
            counts++
        }else{
            logoAnimation.pause()
            imgButton.setIconResource(R.drawable.ic_play_circle_2)
        }
    }
    private fun initView(){
        imgButton=binding.btnPlay
    }
    private fun initListener() {
        headerLayout.setOnClickListener{
            findNavController(R.id.app_nav).navigate(R.id.action_mainFragment_to_loginFragment)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 ->{
                    Toast.makeText(this, "我的消息", Toast.LENGTH_SHORT).show()
                }
                R.id.item2 -> {
                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
                }
                R.id.item3 -> {
                    Toast.makeText(this, "深色模式", Toast.LENGTH_SHORT).show()
                }
                R.id.item4 ->{
                    Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, MusicService::class.java)
        stopService(intent)
        songDao.updateIsPlayingBySelf(false)
        Log.e("MainActivity","onDestroy")
        unbindService(mServiceConnection)
    }
    fun onClick() {
        //"播放".showToast(this)
        val id=if (prefs.getInt("mode",0)!=2){
            songDao.loadId()
        }else{
            randomDao.loadLastPlayingSongId()
        }
        if (counts%2!=0){
            imgButton.setIconResource(R.drawable.ic_play_circle_2)
            //musicService!!.pauseOrContinueMusic()
            musicService!!.pauseOrContinue()
            logoAnimation.pause()
            songDao.updateIsPlayingBySelf(false)
        }else{
            imgButton.setIconResource(R.drawable.ic_pause)

            if (counts!=0){
                //musicService!!.pauseOrContinueMusic()
                musicService!!.pauseOrContinue()
            }else{
                if (id != null) {
                    musicService!!.play(id-1,0)
                    songDao.updateIsPlaying(true, lastPlay = true)
                }else{
                    musicService!!.play(0,0)
                }
            }
            if (logoAnimation.isPaused) {
                logoAnimation.resume()
            }else{
                logoAnimation.start()
            }
        }
        counts++
        thread {
            for (song in randomDao.loadAllRandomSong()){
                Log.e("MainActivity",song.toString()+"id:${song.id}")
            }
            for (random in randomDao.loadAllRandom()){
                Log.e("MainActivity",random.toString()+"id:${random.id}")
            }
        }
    }
}