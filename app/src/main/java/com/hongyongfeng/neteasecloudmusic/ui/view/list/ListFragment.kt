package com.hongyongfeng.neteasecloudmusic.ui.view.list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.ListAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentListBinding
import com.hongyongfeng.neteasecloudmusic.model.Artist
import com.hongyongfeng.neteasecloudmusic.model.Detail
import com.hongyongfeng.neteasecloudmusic.model.dao.RandomDao
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayListDetailedInterface
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.neteasecloudmusic.util.RandomNode
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class ListFragment: BaseFragment<FragmentListBinding, SearchViewModel>(
    FragmentListBinding::inflate,
    SearchViewModel::class.java,
    true
){
    private lateinit var mPublicViewModel: PublicViewModel
    private var page=1
    private lateinit var recyclerView:RecyclerView
    private lateinit var binding: FragmentListBinding
    private lateinit var mActivity: FragmentActivity
    private lateinit var mSearchViewModel: SearchViewModel
    private var listSongs= mutableListOf<Detail>()
    private var adapter= ListAdapter(listSongs)
    private lateinit var songDao:SongDao
    private lateinit var randomDao:RandomDao
    private lateinit var prefs: SharedPreferences
    var isScroll=false
    private lateinit var mView:View
    private var matrix: Matrix? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun initFragment(
        binding: FragmentListBinding,
        viewModel: SearchViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        mActivity=requireActivity()
        prefs=mActivity.getSharedPreferences("player", Context.MODE_PRIVATE)
        songDao=AppDatabase.getDatabase(mActivity).songDao()
        randomDao=AppDatabase.getDatabase(mActivity).randomDao()
        if (publicViewModel!=null){
            this.mPublicViewModel=publicViewModel
        }
        if (viewModel!=null){
            this.mSearchViewModel=viewModel
        }
        recyclerView=binding.rvPlaylist
        initView(binding, StatusBarUtils.getStatusBarHeight(activity as AppCompatActivity)+10)
        SetRecyclerView.setRecyclerView(
            mActivity,
            recyclerView,
            adapter
        )
        with(binding){
            toolbarPlaylistFragment.title=arguments?.getString("listName")
            tvName.text=arguments?.getString("listName")
            tvCreator.text=arguments?.getString("creator")
            //使用Glide异步获取bitmap，提供bitmap加载图片的同时，让palette进行颜色获取

            Glide.with(activity as AppCompatActivity).asBitmap().load(arguments?.getString("url")).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    //防止该 Fragment 已经销毁后Glide异步加载，导致崩溃
                    //if (!viewDestroy) {
                        val compressBitmap = getCompressBitmap(0.5f, 0.5f, bitmap)
                        imgAlbum.setImageBitmap(compressBitmap)
                        Palette.from(compressBitmap).generate { palette ->
                            val darkMutedColor = palette?.getDarkMutedColor(Color.GRAY) ?: Color.GRAY
                            val mutedColor = palette?.getMutedColor(Color.GRAY) ?: Color.GRAY
                            //背景设置渐变
                            val gradientDrawable = GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                arrayOf(darkMutedColor, mutedColor).toIntArray()
                            )
                            appBarLayoutPlaylistFragment.background = gradientDrawable
                            collapsingToolbarLayoutPlaylistFragment.setContentScrimColor((mutedColor + darkMutedColor) / 2)
                        }
                    //}
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    //if (!viewDestroy) Glide.with(requireContext()).clear(this)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }
            })
        }

//        Picasso.get().load(arguments?.getString("url")).fit().into(binding.imgAlbum)


    }
    /**
     * 通过Matrix对Bitmap进行压缩
     * @param sx 对水平方向的压缩量
     * @param sy 对垂直方向的压缩量
     * @return 返回压缩完成的Bitmap
     */
    fun getCompressBitmap(sx: Float, sy: Float, bitmap: Bitmap): Bitmap{
        if (matrix == null) matrix = Matrix()
        matrix!!.setScale(sx, sy)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    override fun onStart() {
        super.onStart()
        if (listSongs.isEmpty()){
            listRequest(arguments?.getLong("id")!!,0)
        }
    }
    private fun listRequest(id:Long,page:Int){
        mPublicViewModel.apply {
            getAPI(PlayListDetailedInterface::class.java).getPlayListDetailed(id,30,(page)*30).getResponse {
                    flow ->
                flow.collect{
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAG",it.errMsg)
                            adapter.notifyItemChanged(listSongs.size-1)
                            withContext(Dispatchers.Main){
                                Toast.makeText(mActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val songsList=it.response.songs
                            if (songsList.isNotEmpty()){
                                listSongs.addAll(songsList)
                                isScroll=false
                            }else{
                                val view = recyclerView.getChildAt(recyclerView.childCount - 1)
                                if (view != null) {
                                    val bar = view.findViewById<ProgressBar>(R.id.progress_bar)
                                    bar.visibility = View.GONE
                                    val tv = view.findViewById<TextView>(R.id.tv_load)
                                    tv.text="没有更多内容了"
                                    isScroll=true
                                }
                            }
                            adapter.notifyItemChanged(listSongs.size)
                        }
                    }
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    override fun initListener() {
//        binding.tvBack.setOnClickListener {
//            mActivity.onBackPressed()
//        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollExtent() != recyclerView.computeVerticalScrollRange()) {
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                        mView = recyclerView.getChildAt(recyclerView.childCount - 1)
                        if (!isScroll){
                            val bar = mView.findViewById<ProgressBar>(R.id.progress_bar)
                            bar.visibility = View.VISIBLE
                            val tv = mView.findViewById<TextView>(R.id.tv_load)
                            tv.text = "正在加载更多内容"
                            listRequest(arguments?.getLong("id")!!, page)
                            page++
                        }else{
                            val bar = mView.findViewById<ProgressBar>(R.id.progress_bar)
                            bar.visibility = View.GONE
                            val tv = mView.findViewById<TextView>(R.id.tv_load)
                            tv.text = "没有更多内容了"
                        }
                    }
                } else {
                    mView = recyclerView.getChildAt(recyclerView.childCount - 1)
                    val bar = mView.findViewById<ProgressBar>(R.id.progress_bar)
                    bar.visibility = View.GONE
                    val tv = mView.findViewById<TextView>(R.id.tv_load)
                    tv.text = "没有更多内容了"
                }
            }
        })
        adapter.setOnItemClickListener({
                _, position->
            val songs=listSongs[position]
            val intent=Intent(mActivity, PlayerActivity::class.java)
            val bundle=Bundle()
            bundle.putString("name",songs.name)
            bundle.putInt("id",songs.id.toInt())
            val artistList=songs.ar
            val artists=getArtists(artistList)
            thread {
//                songDao.deleteAllSong()
//                songDao.clearAutoIncrease()
                mSearchViewModel.clearTableSong()
                for (songs1 in listSongs){
                    val song =Song(songs1.name,songs1.id*1L,songs1.al.id*1L,getArtists(songs1.ar), albumUrl = songs1.al.picUrl)
                    song.id=mSearchViewModel.insertSong(song)
                    if(song.id-1==position*1L){
                        song.isPlaying=true
                        //以下使得lastPlaying为true的代码要在service中切歌的时候再写一遍
                        song.lastPlaying=true
                        mSearchViewModel.updateSong(song)
                    }
                }
                if (mSearchViewModel.getPlayMode()==2){
                    val max=mSearchViewModel.selectMaxId().toInt()
                    RandomNode.randomList(max,mActivity)
                    bundle.putInt("position",(mSearchViewModel.loadRandomBySongId(position+1)!!.id-1).toInt())
                }else{
                    bundle.putInt("position",position)
                }
                val albumId=songs.al.id
                bundle.putInt("albumId",albumId.toInt())
                bundle.putString("singer",artists)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }){
                _, position->
            listSongs[position].apply {
                val artists=getArtists(ar)
                val song=Song(name, id  , al.id,artists, albumUrl = al.picUrl)
                mSearchViewModel.loadLastPlayingSong().apply {
                    song.id= ((this?.id)?.plus(1)) ?: 1
                    if (this?.id!=null){
                        val max=mSearchViewModel.selectMaxId()
                        for (mId in max downTo this.id+1){
                            mSearchViewModel.plusSongById(mId)
                        }
                    }
                }
                mSearchViewModel.insertSong(song)
                ("已添加到下一首播放").showToast(mActivity)
            }
        }
    }
    private fun getArtists(artistList:List<Artist>?):String{
        val artists=java.lang.StringBuilder()
        for (artist in artistList!!){
            if (artist == artistList[artistList.size-1]){
                artists.append(artist.name)
            }else{
                artists.append(artist.name).append("/")
            }
        }
        return artists.toString()
    }
    private fun initView(binding: FragmentListBinding, dp:Int){
//        val lp = binding.collapsingToolbarLayoutPlaylistFragment.layoutParams as AppBarLayout.LayoutParams
//        lp.topMargin= dp
//        binding.collapsingToolbarLayoutPlaylistFragment.layoutParams = lp
        val lp = binding.toolbarPlaylistFragment.layoutParams as CollapsingToolbarLayout.LayoutParams
        lp.topMargin= dp
        binding.toolbarPlaylistFragment.layoutParams = lp
//        val lp = binding.appBarLayoutPlaylistFragment.layoutParams as CoordinatorLayout.LayoutParams
//        lp.topMargin= dp
//        binding.appBarLayoutPlaylistFragment.layoutParams = lp

        val lpConstraintLayout = binding.constraintLayoutList.layoutParams as CollapsingToolbarLayout.LayoutParams
        lpConstraintLayout.topMargin= dp+lp.height
        binding.constraintLayoutList.layoutParams = lpConstraintLayout
    }
    private fun initToolbar(){
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarPlaylistFragment)
            supportActionBar?.apply {
                title = "歌单"
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            mActivity.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}