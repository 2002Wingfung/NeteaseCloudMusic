package com.hongyongfeng.neteasecloudmusic.ui.view.recently

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentRecentlyBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class RecentlyFragment: BaseFragment<FragmentRecentlyBinding, ViewModel>(
    FragmentRecentlyBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentRecentlyBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {

    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}