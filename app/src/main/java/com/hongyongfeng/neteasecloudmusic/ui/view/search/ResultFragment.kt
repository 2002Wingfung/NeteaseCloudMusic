package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.SongsAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentResultBinding
import com.hongyongfeng.neteasecloudmusic.model.Artists
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.model.dao.RandomDao
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.SearchInterface
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.neteasecloudmusic.util.RandomNode
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class ResultFragment : BaseFragment<FragmentResultBinding, SearchViewModel>(
    FragmentResultBinding::inflate,
    SearchViewModel::class.java,
    true
){
    private var page=1
    private var listSongs= mutableListOf<Songs>()
    private var adapter= SongsAdapter(listSongs)
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding:FragmentResultBinding
    private lateinit var mActivity: FragmentActivity
    private lateinit var mPublicViewModel: PublicViewModel
    private lateinit var mSearchViewModel: SearchViewModel
    private lateinit var songDao:SongDao
    private lateinit var randomDao: RandomDao
    private lateinit var prefs: SharedPreferences
    var isScroll=false

    override fun initFragment(
        binding: FragmentResultBinding,
        viewModel: SearchViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        recyclerView = binding.rvSongs
        if (publicViewModel != null) {
            this.mPublicViewModel=publicViewModel
        }
        if (viewModel!=null){
            this.mSearchViewModel=viewModel
        }
        mActivity=requireActivity()
        prefs=mActivity.getSharedPreferences("player", Context.MODE_PRIVATE)
        songDao=AppDatabase.getDatabase(mActivity).songDao()
        randomDao=AppDatabase.getDatabase(mActivity).randomDao()
        SetRecyclerView.setRecyclerView(
            mActivity,
            recyclerView,
            adapter
        )
    }
    override fun onStart() {
        super.onStart()
        if (listSongs.isEmpty()){
            searchRequest(arguments?.getString("text")!!,0)
        }
    }

    override fun initListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.computeVerticalScrollExtent() != recyclerView.computeVerticalScrollRange()) {
                        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                            val mView = recyclerView.getChildAt(recyclerView.childCount - 1)
                            if (!isScroll){
                                val bar = mView.findViewById<ProgressBar>(R.id.progress_bar)
                                bar.visibility = View.VISIBLE
                                val tv = mView.findViewById<TextView>(R.id.tv_load)
                                tv.text = "正在加载更多内容"
                                searchRequest(arguments?.getString("text")!!, page)
                                page++
                            }else{
                                val bar = mView.findViewById<ProgressBar>(R.id.progress_bar)
                                bar.visibility = View.GONE
                                val tv = mView.findViewById<TextView>(R.id.tv_load)
                                tv.text = "没有更多内容了"
                            }
                        }
                    } else {
                        val view = recyclerView.getChildAt(recyclerView.childCount - 1)
                        if (view != null) {
                            val bar = view.findViewById<ProgressBar>(R.id.progress_bar)
                            bar.visibility = View.GONE
                            val tv = view.findViewById<TextView>(R.id.tv_load)
                            tv.text = "没有更多内容了"
                        }
                    }
                }
        })
        adapter.setOnItemClickListener({ _, position ->
            val songs = listSongs[position]
            val intent = Intent(mActivity, PlayerActivity::class.java)
            val bundle = Bundle()
            bundle.putString("name", songs.name)
            bundle.putInt("id", songs.id)
            val artistList = songs.getArtists()
            val artists = getArtists(artistList)
            thread {
                mSearchViewModel.clearTableSong()
//                songDao.deleteAllSong()
//                songDao.clearAutoIncrease()
                for (songs1 in listSongs) {
                    val song = Song(
                        songs1.name,
                        songs1.id * 1L,
                        songs1.getAlbum()!!.id * 1L,
                        getArtists(songs1.getArtists())
                    )
                    song.id =mSearchViewModel.insertSong(song)
                    //song.id = songDao.insertSong(song)
                    if (song.id - 1 == position * 1L) {
                        song.isPlaying = true
                        //以下使得lastPlaying为true的代码要在service中切歌的时候再写一遍
                        song.lastPlaying = true
                        //songDao.updateSong(song)
                        mSearchViewModel.updateSong(song)
                    }
                }
                //if (prefs.getInt("mode",-1)==2){
                if (mSearchViewModel.getPlayMode()==2){
                    //val max=songDao.selectMaxId().toInt()
                    val max=mSearchViewModel.selectMaxId().toInt()
                    RandomNode.randomList(max,mActivity)
                    //bundle.putInt("position",(randomDao.loadRandomBySongId(position+1).id-1).toInt())
                    bundle.putInt("position",((mSearchViewModel.loadRandomBySongId(position + 1)!!.id  - 1)).toInt())
                }else{
                    bundle.putInt("position",position)
                }
                val albumId = songs.getAlbum()!!.id
                bundle.putInt("albumId", albumId)
                bundle.putString("singer", artists)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }) { _, position ->
            listSongs[position].apply {
                val artists = this@ResultFragment.getArtists(getArtists())
                val song = Song(name, id.toLong(), getAlbum()?.id?.toLong()?:0, artists)
//                songDao.loadLastPlayingSong().apply {
                mSearchViewModel.loadLastPlayingSong().apply {
                    song.id = ((this?.id)?.plus(1)) ?: 1
                    if (this?.id != null) {
                        //val max = songDao.selectMaxId()
                        val max = mSearchViewModel.selectMaxId()
                        for (mId in max downTo this.id + 1) {
//                            songDao.plusSongById(mId)
                            mSearchViewModel.plusSongById(mId)
                        }
                    }
                }
                //songDao.insertSong(song)
                mSearchViewModel.insertSong(song)
                ("已添加到下一首播放").showToast(mActivity)
            }
        }
    }
    private fun getArtists(artistList: List<Artists>?): String {
        val artists = java.lang.StringBuilder()
        for (artist in artistList!!) {
            if (artist == artistList[artistList.size - 1]) {
                artists.append(artist.name)
            } else {
                artists.append(artist.name).append("/")
            }
        }
        return artists.toString()
    }
    private fun searchRequest(string: String,page:Int) {
        mPublicViewModel.apply {
            getAPI(SearchInterface::class.java).getSearchData(string,30,(page)*30).getResponse {
                    flow ->
                flow.collect {
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
                            val songsList=it.response.result.songs
                            if(songsList.isNotEmpty()){
                                listSongs.addAll(songsList)
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
}