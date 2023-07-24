package com.hongyongfeng.neteasecloudmusic.ui.view.list

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.adapter.SongsAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentListBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentRecentlyBinding
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.RecentlyViewModel

class ListFragment: BaseFragment<FragmentListBinding, ViewModel>(
    FragmentListBinding::inflate,
    null,
    true
){
    private lateinit var binding: FragmentListBinding
    private lateinit var mActivity: FragmentActivity
    private var listSongs= mutableListOf<Songs>()
    private var adapter= SongsAdapter(listSongs)
    override fun initFragment(
        binding: FragmentListBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        mActivity=requireActivity()
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}