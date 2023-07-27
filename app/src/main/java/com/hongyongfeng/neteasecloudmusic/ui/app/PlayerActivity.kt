package com.hongyongfeng.neteasecloudmusic.ui.app

import com.hongyongfeng.neteasecloudmusic.livedata.LiveDataBus
import com.hongyongfeng.neteasecloudmusic.livedata.LiveDataBus.BusMutableLiveData
import android.animation.ObjectAnimator
import android.content.*
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hongyongfeng.neteasecloudmusic.*
import com.hongyongfeng.neteasecloudmusic.adapter.SongAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityPlayerBinding
import com.hongyongfeng.neteasecloudmusic.model.dao.RandomDao
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayerInterface
import com.hongyongfeng.neteasecloudmusic.service.MusicService
import com.hongyongfeng.neteasecloudmusic.service.MusicService.Companion.mediaPlayer
import com.hongyongfeng.neteasecloudmusic.util.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt


class PlayerActivity :BaseActivity<ActivityPlayerBinding,ViewModel>(
    ActivityPlayerBinding::inflate,true
){
    private lateinit var imageLoader: ImageLoader

    /**
     * 当Service中通知栏有变化时接收到消息
     */
    private var activityLiveData: BusMutableLiveData<String>? = null
    private var albumId: Int=0

    private lateinit var mAnimator: ObjectAnimator
    private lateinit var mAnimatorNeedlePause: ObjectAnimator
    private lateinit var mAnimatorNeedleStart: ObjectAnimator
    private lateinit var seekBar: SeekBar
    private lateinit var timer:Timer
    private var count=0
    private var position :Int?=0
    private lateinit var prefs: SharedPreferences
    private var listSongs= mutableListOf<Song>()
    private var adapter= SongAdapter(listSongs)
    private lateinit var songDao:SongDao
    private lateinit var randomDao: RandomDao

    /**
     * 当在Activity中做出播放模式的改变时，通知做出相应改变
     */
    private var modeLiveData: BusMutableLiveData<String>? = null
    private var status:Int?=0
    private var first= mutableListOf(1)
    private lateinit var myService: MusicService
    companion object {
        const val ACTION_SERVICE_PERCENT: String="action.percent"
        const val ACTION_SERVICE_NEED: String="action.ServiceNeed"
        const val ACTION_SERVICE_COMPLETE: String="action.ServiceComplete"
    }
    private var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            myService = (service as MusicService.MediaPlayerBinder).getMusicService()
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    internal inner class CompleteReceiver:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            timer.cancel()
            "播放已完成".showToast(this@PlayerActivity)
        }

    }
    internal inner class PercentReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val percent=intent?.getIntExtra("percent",-1)
            val duration= mediaPlayer.duration
            seekBar.max=duration
            seekBar.secondaryProgress = percent!!*duration/100
        }
    }
    internal inner class ServiceBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val duration= mediaPlayer.duration
            seekBar.max=duration
            refresh(seekBar, mediaPlayer)
        }
    }
    private fun time(time:Int):String{
        val min=(time/1000f/60).toInt()
        val s=(time-min*60*1000)/1000f.roundToInt()

        return if (min==1&&s==10){
            "0$min:$s"
        }else if(min==0&&s==10){
            "0$min:$s"
        }else if(min<10&&s<10){
            "0$min:0$s"
        }else if (min<10&&s>10){
            "0$min:$s"
        }else if (min>10&&s<10){
            "$min:0$s"
        }else{
            "$min:$s"
        }

    }
    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what==0){
                mAnimator.pause()
            }else{
                mAnimator.resume()
            }
        }
    }
    private fun initAnimation(){
        // Object target:目标对象，
        // String propertyName:指定要改变对象的什么属性，这个属性名要求在对应对象中必须有对应的public的PsetPropertyName的方法。如上面的rotation就要求ImageView中必须有setRotation方法才行。
        // float... values:一系列这个属性将会到达的值
        mAnimator= ObjectAnimator.ofFloat(binding.imgAlbum, "rotation", 0f, 720f)
        val needle=binding.imgNeedle
        mAnimatorNeedlePause= ObjectAnimator.ofFloat(needle, "rotation", 0f, -30f)
        mAnimatorNeedleStart= ObjectAnimator.ofFloat(needle, "rotation", -30f, 0f)
        needle.pivotX=needle.width/1f+62
        needle.pivotY=needle.height*1f+135
        mAnimatorNeedlePause.duration = 700
        mAnimatorNeedlePause.interpolator = LinearInterpolator()
        mAnimatorNeedleStart.duration = 700
        mAnimatorNeedleStart.interpolator = LinearInterpolator()
        // Object target:目标对象，
        // String propertyName:指定要改变对象的什么属性，这个属性名要求在对应对象中必须有对应的public的PsetPropertyName的方法。如上面的rotation就要求ImageView中必须有setRotation方法才行。
        // float... values:一系列这个属性将会到达的值
        // 设置一次动画的时间
        // 设置一次动画的时间
        mAnimator.duration = 19000
        // 设置插值器，用来控制变化率
        // 设置插值器，用来控制变化率
        mAnimator.interpolator = LinearInterpolator()
        // 设置重复的次数，无限
        mAnimator.repeatCount = ObjectAnimator.INFINITE
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //imageLoader= ImageLoader.build(this)
        initAnimation()
        prefs=getSharedPreferences("player", Context.MODE_PRIVATE)
        songDao= AppDatabase.getDatabase(this@PlayerActivity).songDao()
        randomDao= AppDatabase.getDatabase(this@PlayerActivity).randomDao()
        val bundle = intent.extras
        status=bundle?.getInt("status")

        position=bundle?.getInt("position")
        val name=bundle?.getString("name")
        binding.tvTitle.text=name
        val albumIdResult=bundle?.getInt("albumId")
        if (albumIdResult!=null){
            albumId=albumIdResult
            picRequest(albumId)
        }
        val intent1 = Intent(this@PlayerActivity, MusicService::class.java)
        bindService(
            intent1, mServiceConnection,
            BIND_AUTO_CREATE
        )

        val songId=bundle?.getInt("id")
        val singer=bundle?.getString("singer")

        if (songId!=null){
            //储存id到sp，然后每次进行oncreate方法的时候就读取这个id，如果这个id和sp中的一样，则不重置mediaplayer，
            //如果不一样则重置
            val prefs=getSharedPreferences("player",Context.MODE_PRIVATE)
            val songIdOrigin=prefs.getInt("songId",-1)
            if (songIdOrigin==-1){
                songsRequest(songId)
                prefs.edit{
                    putInt("songId",songId)
                    putString("songName",name)
                    putString("singer",singer)
                    putInt("albumId",albumId)
                }
            }else if (songIdOrigin!=songId){
                Log.e("MyPlayerActivity","与上一首不是同一首歌")
                if (mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }else{
                    mediaPlayer.reset()
                }
                //if (::mediaPlayer.isInitialized){

                //}else{
                //Log.e("MyPlayerActivity","not init")
                //现在播放不同的歌曲会导致上一首歌曲不能停止
                //}
                songsRequest(songId)

                prefs.edit{
                    putInt("songId",songId)
                }
            }else
            {
                Log.e("MyPlayerActivity","是同一首歌")
                if (status!=-1){
                    if (!mediaPlayer.isPlaying){
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        songsRequest(songId)
                    }
                }else{
                    thread {
                        val song=songDao.loadIsPlayingSong()
                        if(song==null){
                            count++
                            runOnUiThread{
                                handler.sendEmptyMessageDelayed(0, 700)
                                val duration=mediaPlayer.duration
                                seekBar.max= duration
                                binding.tvTotal.text=time(duration)
                                refresh(seekBar, mediaPlayer)
                                mAnimatorNeedleStart.pause()
                                mAnimatorNeedlePause.start()
                                //"暂停".showToast(this)
                                binding.icPlay.background=  getDrawable(R.drawable.ic_play_circle_2)
                            }
                        }else{
                            runOnUiThread{
                                val duration=mediaPlayer.duration
                                binding.tvTotal.text=time(duration)
                            }
                        }
                    }
                }
            }
        }
        binding.tvSinger.text=singer
        transparentNavBar(this)
        initView(binding, StatusBarUtils.getStatusBarHeight(this as AppCompatActivity)+5)
        initListener()
        mAnimator.start()
        //当音频文件加载好之后就start
    // 在合适的位置调用 mAnimator.pause()方法进行暂停操作
        notificationObserver()
        register()
        prefs.getInt("mode",-1).apply {
            if (this!=-1){
                count1+=this
                when(this){
                    0->binding.icMode.setBackgroundResource(R.drawable.ic_order)
                    1->binding.icMode.setBackgroundResource(R.drawable.ic_repeat)
                    2->binding.icMode.setBackgroundResource(R.drawable.ic_random)
                }
            }else{
                prefs.edit{
                    putInt("mode",0)
                }
            }
        }
    }
    private fun register(){
        val filterPercent = IntentFilter()
        filterPercent.addAction(ACTION_SERVICE_PERCENT)

        registerReceiver(PercentReceiver(), filterPercent);
        val filterComplete = IntentFilter()
        filterComplete.addAction(ACTION_SERVICE_COMPLETE)
        registerReceiver(CompleteReceiver(), filterComplete);
        val filter = IntentFilter()
        filter.addAction(ACTION_SERVICE_NEED)

        registerReceiver(ServiceBroadcastReceiver(), filter);
    }
    private fun initView(binding: ActivityPlayerBinding, dp:Int){
        val lp = binding.layoutActionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutActionBar.layoutParams = lp
        seekBar=binding.seekBar
        if (mediaPlayer.isPlaying){
            seekBar.max=mediaPlayer.duration
            refresh(seekBar, mediaPlayer)
        }
        modeLiveData=LiveDataBus.instance.with("mode_control",String::class.java)
    }
    /**
     * 通知栏动作观察者
     */
    private fun notificationObserver() {
        val songDao = AppDatabase.getDatabase(this).songDao()
        activityLiveData = LiveDataBus.instance.with("activity_control", String::class.java)
        activityLiveData!!.observe(
            this@PlayerActivity, true
        ) { value ->
            when (value) {

                PLAY -> {
                    runOnUiThread{
                        binding.icPlay.setBackgroundResource(R.drawable.ic_pause)
                        if (mAnimator.isPaused) {
                            mAnimator.resume()
                        } else {
                            mAnimator.start()
                        }
                    }
                }
                PAUSE, CLOSE -> {
                    runOnUiThread {
                        mAnimator.pause()
                        binding.icPlay.setBackgroundResource(R.drawable.ic_play_circle_2)
                    }
                }
                PREV -> {
                    runOnUiThread {
                        val song = songDao.loadLastPlayingSong()
                        binding.tvTitle.text = song?.name
                        binding.tvSinger.text=song?.artist
                        Picasso.get().load(song?.albumUrl).fit().into(binding.imgAlbum)
                    }
                }
                NEXT -> {
                    runOnUiThread {
                        val song = songDao.loadLastPlayingSong()
                        binding.tvTitle.text = song?.name
                        binding.tvSinger.text=song?.artist
                        Picasso.get().load(song?.albumUrl).fit().into(binding.imgAlbum)
                    }
                }
                "prepared"->{
                    runOnUiThread {
                        val duration= mediaPlayer.duration
                        seekBar.max=duration
                        refresh(seekBar, mediaPlayer)
                    }
                }
                else -> {
                    val percent=value.toInt()
                    val duration= mediaPlayer.duration
                    runOnUiThread {
                        seekBar.max=duration
                        binding.tvTotal.text=time(duration)
                        seekBar.secondaryProgress = percent *duration/100
                    }
                }
            }
        }
    }
    private fun songsRequest(songId:Int){
        //请求音频文件的代码
        //先获取音频的url，再进行解析
        publicViewModel!!.apply {
            getAPI(PlayerInterface::class.java).getSong(songId.toString()).getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAGInternet",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@PlayerActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                                handler.sendEmptyMessageDelayed(0, 700)
                                mAnimatorNeedleStart.pause()

                                mAnimatorNeedlePause.start()
                                binding.icPlay.background=  getDrawable(R.drawable.ic_play_circle_2)
                                if (mediaPlayer.isPlaying){
                                    mediaPlayer.pause()//暂停播放
                                }
                                count++
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val url=it.response.data[0].url

                            val intent = Intent(this@PlayerActivity, MusicService::class.java)
                            intent.putExtra("url",url)
                            //intent.putExtra("time",it.response.data[0].time)
                            binding.tvTotal.text=time(it.response.data[0].time)
                            //Log.e("serviceMusic","Start")
                            intent.putExtra("position",position)
                            startService(intent)
                            //val songDao=AppDatabase.getDatabase(this@PlayerActivity).songDao()
                            //myService.updateNotificationShow(songDao.loadId()-1)
                            //在点击列表的时候把当前列表的List保存到数据库中，用room
                            //在数据库表中加一列做是否当前正在播放，将正在播放的显示在MainActivity的底部播放器和通知栏播放器中
                        }
                    }
                }
            }
        }
    }
    private fun picRequest(albumId:Int){
        //请求专辑图片的代码
        //先获取图片的url，再进行解析，然后转化成Bitmap
        publicViewModel!!.apply {
            getAPI(PlayerInterface::class.java).getAlbum(albumId.toString()).getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAGInternet",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@PlayerActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val url=it.response.songs[0].al.picUrl
                            //Picasso.get().load(url).resize(512,512).into(binding.imgAlbum)
                            Picasso.get().load(url).fit().into(binding.imgAlbum)
                            thread {
                                songDao.updateAlbumUrl(url,albumId)
                            }
//                            imageLoader.bindBitmap(
//                                url,
//                                binding.imgAlbum,
//                                1024,
//                                1024
//                            )
                        }
                    }
                }
            }
        }
    }
    var count1=0

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.icPlay.setOnClickListener {
            if (count%2==0){
                handler.sendEmptyMessageDelayed(0, 700)
                mAnimatorNeedleStart.pause()
                mAnimatorNeedlePause.start()
                //"暂停".showToast(this)
                it.background=  getDrawable(R.drawable.ic_play_circle_2)
                if (mediaPlayer.isPlaying){
                    myService.pauseOrContinueMusic()
                    thread {
                        //将isPlaying设为false
                        songDao.updateIsPlaying(false, lastPlay = true)
                        songDao.updateIsPlayingBySelf(false)
                    }
                }
            }else{
                handler.sendEmptyMessageDelayed(1, 700)
                mAnimatorNeedlePause.pause()
                mAnimatorNeedleStart.start()
                it.background = getDrawable(R.drawable.ic_pause)

                if (!mediaPlayer.isPlaying){
                    if (status==-1&& mediaPlayer.currentPosition==0){
                        if (prefs.getInt("mode",0)==2){
                            myService.play(randomDao.loadLastPlayingSongId()?.minus(1)?:0,0)
                        }else{
                            myService.play(songDao.loadLastPlayingSong()?.id?.toInt()?.minus(1) ?: 0,0)
                        }
                        songDao.updateIsPlaying(true, lastPlay = true)
                    }else{
                        //开始播放
                        myService.pauseOrContinueMusic()
                        thread {
                            songDao.updateIsPlaying(true, lastPlay = true)
                        }
                    }
                }
            }
            count++
        }
        binding.icNext.setOnClickListener {
            myService.nextMusic()
            if (mAnimator.isPaused){
                mAnimatorNeedleStart.start()
                mAnimator.start()
                binding.icPlay.setBackgroundResource(R.drawable.ic_pause)
                count++
            }
        }
        binding.icBack.setOnClickListener {
            myService.previousMusic()
            //切歌的时候判断动画是否在转，如果在暂停状态，则开启
            //Picasso.get().load(url).fit().into(binding.imgAlbum)
            if (mAnimator.isPaused){
                mAnimator.start()
                mAnimatorNeedleStart.start()
                binding.icPlay.setBackgroundResource(R.drawable.ic_pause)
                count++
            }
        }
        binding.icMode.apply {
            setOnClickListener {
                when(count1%3){
                    0-> {
                        this.setBackgroundResource(R.drawable.ic_repeat)
                        prefs.edit{
                            putInt("mode",1)
                        }
                    }
                    1->{
                        //随机
                        this.setBackgroundResource(R.drawable.ic_random)
                        prefs.edit{
                            putInt("mode",2)
                        }
                        val max=songDao.selectMaxId().toInt()
                        RandomNode.randomList(max,this@PlayerActivity)
                        modeLiveData?.postValue(RANDOM)

                    }
                    2->{
                        //顺序
                        this.setBackgroundResource(R.drawable.ic_order)
                        prefs.edit{
                            putInt("mode",0)
                        }
                        modeLiveData?.postValue(ORDER)
                    }
                }
                count1++
            }
        }

        binding.icList.setOnClickListener {
            listSongs.addAll(songDao.loadAllSongs())
            showBottomSheetDialog()
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvCurrent.text=time(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                MusicService.isChanging=true;
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //当拖动停止后，控制mediaPlayer播放指定位置的音乐
                seekBar!!.max= mediaPlayer.duration
                mediaPlayer.seekTo(seekBar.progress)
                MusicService.isChanging=false;
            }
        })
        adapter.setOnItemClickListener({
                view,position->

//            val intent= Intent(mActivity, PlayerActivity::class.java)
//            val bundle=Bundle()
//            val song=listSongs[position]
//            bundle.putString("name",song.name)
//            bundle.putInt("id",song.songId.toInt())
//            thread {
//                songDao.updateIsPlaying(false, lastPlay = true)
//                songDao.updateLastPlaying(false, origin = true)
//                songDao.updateLastPlayingById(true,song.id)
//                songDao.updateIsPlaying(true, lastPlay = true)
//                song.isPlaying=true
//                //以下使得lastPlaying为true的代码要在service中切歌的时候再写一遍
//                song.lastPlaying=true
//                songDao.updateSong(song)
//            }
//            bundle.putInt("albumId",song.albumId.toInt())
//            bundle.putInt("position",position)
//            bundle.putString("singer",song.artist)
//            intent.putExtras(bundle)
//            startActivity(intent)
        },{
                view,position->
            //("删除$position").showToast(mActivity)
            songDao.deleteSongById(position+1)
            songDao.updateSongById(position+2)
            listSongs.removeAt(position)
            adapter.notifyItemRemoved(position)
        },{
                view,position->
            ("已添加到下一首播放").showToast(this)

        })
    }
    private fun refresh(seekBar: SeekBar, mediaPlayer:MediaPlayer){
        timer=Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                if (!MusicService.isChanging){
                    //当用户正在拖动进度进度条时不处理进度条的的进度
                    seekBar.progress = mediaPlayer.currentPosition
                }
            }
        },0,500)
    }
    private fun showBottomSheetDialog(){
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet)
        val clear: TextView? = bottomSheetDialog.findViewById(R.id.tv_back)
        val recyclerView: RecyclerView? = bottomSheetDialog.findViewById(R.id.rv_play_list)
        SetRecyclerView.setRecyclerView(
            this,
            recyclerView,
            adapter
        )
        clear!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
    override fun onDestroy() {
        if (::timer.isInitialized){
            timer.cancel()
        }

        Log.e("MyPlayerActivity","onDestroy")
        val intent = Intent(this@PlayerActivity, MusicService::class.java)

        stopService(intent)
        unbindService(mServiceConnection)
        super.onDestroy()
    }
}