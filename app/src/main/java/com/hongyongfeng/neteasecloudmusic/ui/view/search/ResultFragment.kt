package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.content.Intent
import android.os.Bundle
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
import com.hongyongfeng.neteasecloudmusic.network.api.HotInterface
import com.hongyongfeng.neteasecloudmusic.network.api.SearchInterface
import com.hongyongfeng.neteasecloudmusic.ui.app.PlayerActivity
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultFragment : BaseFragment<FragmentResultBinding, SearchViewModel>(
    FragmentResultBinding::inflate,
    SearchViewModel::class.java,
    true
){
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
        if (viewModel != null) {
            this.viewModel=viewModel
        }
    }

    private fun initView(){
        recyclerView = binding.rvSongs

    }

    override fun onStart() {
        super.onStart()
        if (listSongs.isEmpty()){
            searchRequest(arguments?.getString("text")!!)
        }
    }

    private fun searchRequest(string: String) {
        viewModel!!.apply {
            getAPI(SearchInterface::class.java).getSearchData(string).getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAG",it.errMsg)
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
                            adapter.notifyItemChanged(0)
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
        adapter.setOnItemClickListener { view, position ->
            val songs=listSongs[position]
            println(songs.name)
            val intent=Intent(mActivity, PlayerActivity::class.java)
            val bundle=Bundle()
            bundle.putString("name",songs.name)
            bundle.putInt("id",songs.id)
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }
}