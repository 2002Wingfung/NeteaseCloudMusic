package com.hongyongfeng.neteasecloudmusic.ui.view.recently

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.adapter.SongAdapter
import com.hongyongfeng.neteasecloudmusic.adapter.SongsAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentHotBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentMainBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentRecentlyBinding
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.model.dao.SongDao
import com.hongyongfeng.neteasecloudmusic.model.database.AppDatabase
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.HotViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.RecentlyViewModel
import kotlin.concurrent.thread

class RecentlyFragment: BaseFragment<FragmentRecentlyBinding, RecentlyViewModel>(
    FragmentRecentlyBinding::inflate,
    RecentlyViewModel::class.java,
    true
){
    private lateinit var binding: FragmentRecentlyBinding
    private lateinit var mActivity: FragmentActivity
    private lateinit var viewModel: RecentlyViewModel
    private var listSongs= mutableListOf<Song>()
    private var adapter= SongAdapter(listSongs)
    private lateinit var recyclerView: RecyclerView

    private lateinit var songDao: SongDao
    override fun initFragment(
        binding: FragmentRecentlyBinding,
        viewModel: RecentlyViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {

        this.binding=binding
        recyclerView=binding.rvRecently
        initView(binding, StatusBarUtils.getStatusBarHeight(activity as AppCompatActivity)+10)


        this.viewModel=viewModel!!
        mActivity=requireActivity()
        songDao=AppDatabase.getDatabase(mActivity).songDao()
        viewModel.getSongList {
            if (listSongs.isEmpty()){
                listSongs.addAll(it)
            }
        }
//        viewModel.songList.observe(mActivity){
//            listSongs.addAll(it)
//            adapter.notifyItemChanged(0)
//        }
        SetRecyclerView.setRecyclerView(
            mActivity,
            recyclerView,
            adapter
        )
    }
    private fun initView(binding: FragmentRecentlyBinding, dp:Int){
        val lp = binding.layoutRecently.layoutParams as LinearLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutRecently.layoutParams = lp
    }
    override fun initListener() {
        binding.tvBack.setOnClickListener {
            mActivity.onBackPressed()
        }
        adapter.setOnItemClickListener({
            view,position->

            val intent= Intent(mActivity, PlayerActivity::class.java)
            val bundle=Bundle()
            val song=listSongs[position]
            bundle.putString("name",song.name)
            bundle.putInt("id",song.songId.toInt())
            val songDao= AppDatabase.getDatabase(mActivity).songDao()

            thread {
                songDao.updateIsPlaying(false, lastPlay = true)
                songDao.updateLastPlaying(false, origin = true)
                songDao.updateLastPlayingById(true,song.id)
                songDao.updateIsPlaying(true, lastPlay = true)
                song.isPlaying=true
                //以下使得lastPlaying为true的代码要在service中切歌的时候再写一遍
                song.lastPlaying=true
                songDao.updateSong(song)
            }
            bundle.putInt("albumId",song.albumId.toInt())
            bundle.putInt("position",position)
            bundle.putString("singer",song.artist)
            intent.putExtras(bundle)
            startActivity(intent)
        },{
                view,position->
            //("删除$position").showToast(mActivity)
            songDao.deleteSongById(position+1)
            songDao.updateSongById(position+2)
            listSongs.removeAt(position)
            adapter.notifyItemRemoved(position)
        },{
                _, _ ->

        })
    }
}