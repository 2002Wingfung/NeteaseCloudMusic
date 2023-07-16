package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.adapter.PlayListAdapter
import com.hongyongfeng.neteasecloudmusic.adapter.SongsAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentResultBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class ResultFragment : BaseFragment<FragmentResultBinding, ViewModel>(
    FragmentResultBinding::inflate,
    null,
    true
){
    private var listSongs= mutableListOf<Songs>()
    private var adapter= SongsAdapter(listSongs)
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding:FragmentResultBinding
    private lateinit var mActivity: FragmentActivity

    override fun initFragment(
        binding: FragmentResultBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        println(arguments?.getString("text"))
    }

    private fun initView(){
        recyclerView = binding.rvSongs


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity=requireActivity()
        if (listSongs.size==0){
            listSongs.add(Songs(12,"123",null,null))
        }
        initView()
        SetRecyclerView.setRecyclerViewScroll(
            mActivity,
            recyclerView,
            adapter
        )
    }
    override fun initListener() {
        adapter.setOnItemClickListener { view, position ->
            println(listSongs.get(position).getName())
        }
    }
}