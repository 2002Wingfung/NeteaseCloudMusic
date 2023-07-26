package com.hongyongfeng.neteasecloudmusic.ui.view.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.ListAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentListBinding
import com.hongyongfeng.neteasecloudmusic.model.Artist
import com.hongyongfeng.neteasecloudmusic.model.Detail
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.thread

class ListFragment: BaseFragment<FragmentListBinding, ViewModel>(
    FragmentListBinding::inflate,
    null,
    true
){
    private lateinit var viewModel: PublicViewModel
    private var page=1
    private lateinit var recyclerView:RecyclerView
    private lateinit var binding: FragmentListBinding
    private lateinit var mActivity: FragmentActivity
    private var listSongs= mutableListOf<Detail>()
    private var adapter= ListAdapter(listSongs)
    private lateinit var songDao:SongDao
    override fun initFragment(
        binding: FragmentListBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        mActivity=requireActivity()
        songDao=AppDatabase.getDatabase(mActivity).songDao()
        if (publicViewModel!=null){
            this.viewModel=publicViewModel
        }
        recyclerView=binding.rvPlaylist
        initView(binding, StatusBarUtils.getStatusBarHeight(activity as AppCompatActivity)+10)
        SetRecyclerView.setRecyclerView(
            mActivity,
            recyclerView,
            adapter
        )
        binding.tvListName.text=arguments?.getString("listName")
        binding.tvName.text=arguments?.getString("listName")
        binding.tvCreator.text=arguments?.getString("creator")

        Picasso.get().load(arguments?.getString("url")).fit().into(binding.imgAlbum)
    }

    override fun onStart() {
        super.onStart()
        if (listSongs.isEmpty()){
            listRequest(arguments?.getLong("id")!!,0)
        }
    }
    private fun listRequest(id:Long,page:Int){
        viewModel.apply {
            getAPI(PlayListDetailedInterface::class.java).getPlayListDetailed(id,30,(page)*30).getResponse {
                    flow ->
                flow.collect(){
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
                            }else{
                                val view = recyclerView.getChildAt(recyclerView.childCount - 1)
                                if (view != null) {
                                    val bar = view.findViewById<ProgressBar>(R.id.progress_bar)
                                    bar.visibility = View.GONE
                                    val tv = view.findViewById<TextView>(R.id.tv_load)
                                    tv.text="没有更多内容了"
                                }
                            }
                            adapter.notifyItemChanged(listSongs.size)
                        }
                    }
                }
            }
        }
    }

    override fun initListener() {
        binding.tvBack.setOnClickListener {
            mActivity.onBackPressed()
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollExtent() != recyclerView.computeVerticalScrollRange()) {
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                        listRequest(arguments?.getLong("id")!!,page)
                        page++
                    }
                }
                else{
                    val view = recyclerView.getChildAt(recyclerView.childCount - 1)
                    if (view != null) {
                        val bar = view.findViewById<ProgressBar>(R.id.progress_bar)
                        bar.visibility = View.GONE
                        val tv = view.findViewById<TextView>(R.id.tv_load)
                        tv.text="没有更多内容了"
                    }
                }
            }
        })
        adapter.setOnItemClickListener({
                view,position->
            val songs=listSongs[position]
            val intent=Intent(mActivity, PlayerActivity::class.java)
            val bundle=Bundle()
            bundle.putString("name",songs.name)
            bundle.putInt("id",songs.id.toInt())
            val artistList=songs.ar
            val artists=getArtists(artistList)
            thread {
                songDao.deleteAllSong()
                songDao.clearAutoIncrease()
                for (songs1 in listSongs){
                    val song =Song(songs1.name,songs1.id*1L,songs1.al.id*1L,getArtists(songs1.ar), albumUrl = songs1.al.picUrl)
                    song.id=songDao.insertSong(song)
                    if(song.id-1==position*1L){
                        song.isPlaying=true
                        //以下使得lastPlaying为true的代码要在service中切歌的时候再写一遍
                        song.lastPlaying=true
                        songDao.updateSong(song)
                    }
                }
            }
            val albumId=songs.al.id
            bundle.putInt("albumId",albumId.toInt())
            bundle.putInt("position",position)
            bundle.putString("singer",artists)
            intent.putExtras(bundle)
            startActivity(intent)

        }){
                view,position->
            listSongs[position].apply {
                val artists=getArtists(ar)
                val song=Song(name, id  , al.id,artists, albumUrl = al.picUrl)
                songDao.loadLastPlayingSong().apply {
                    song.id= ((this?.id)?.plus(1)) ?: 1
                    if (this?.id!=null){
                        //songDao.plusSongById(this.id.toInt()+1)
                        val max=songDao.selectMaxId()
                        for (mId in max downTo this.id+1){
                            songDao.plusSongById(mId)
                        }
                    }
                }
                songDao.insertSong(song)
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
        val lp = binding.layoutRecently.layoutParams as LinearLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutRecently.layoutParams = lp
    }



}