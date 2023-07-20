package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.adapter.SongsAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentResultBinding
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.SearchInterface
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    private lateinit var viewModel: SearchViewModel

    override fun initFragment(
        binding: FragmentResultBinding,
        viewModel: SearchViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        recyclerView = binding.rvSongs
        if (viewModel != null) {
            this.viewModel=viewModel
        }
    }

    private fun initView(){


    }

    override fun onStart() {
        super.onStart()
        if (listSongs.isEmpty()){
            searchRequest(arguments?.getString("text")!!,0)
        }
    }

    private fun searchRequest(string: String,page:Int) {
        viewModel!!.apply {
            getAPI(SearchInterface::class.java).getSearchData(string,30,(page)*30).getResponse {
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
                            val songsList=it.response.result.songs
                            //val songsName=it.response
                            //println(String(songsName.bytes()))
                            //println(songsName)

                            listSongs.addAll(songsList)
                            adapter.notifyItemChanged(listSongs.size)

                            //println(listSongs.get(0))
//                            println(list.data)
//                            println(list.code)
                            //切换主线程
                            //更新UI
                            //Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show()
                            //findNavController().navigate(R.id.action_loginFragment_to_mainNavFragment)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity=requireActivity()

        initView()
        SetRecyclerView.setRecyclerView(
            mActivity,
            recyclerView,
            adapter
        )
    }
    override fun initListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollExtent() != recyclerView.computeVerticalScrollRange()) {
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                        //一下代码全都需要改变
                        //"滑动到底部".showToast(mActivity)
                        //listSongs.add(listSongs.get(1))

                        searchRequest(arguments?.getString("text")!!,page)
                        page++
//                        Handler().post {
//                            adapter.notifyItemChanged(listSongs.size - 1)
//                        }

                    }
                }
            }
        })
        adapter.setOnItemClickListener { view, position ->
            val songs=listSongs[position]
            val intent=Intent(mActivity, PlayerActivity::class.java)
            val bundle=Bundle()
            bundle.putString("name",songs.name)
            bundle.putInt("id",songs.id)
            val artistList=songs.getArtists()
            val artists=java.lang.StringBuilder()
            for (artist in artistList!!){
                if (artist == artistList[artistList.size-1]){
                    artists.append(artist.name)
                }else{
                    artists.append(artist.name).append("/")
                }
            }
            val albumId=songs.getAlbum()!!.id
            bundle.putInt("albumId",albumId)
            bundle.putString("singer",artists.toString())
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }
}

