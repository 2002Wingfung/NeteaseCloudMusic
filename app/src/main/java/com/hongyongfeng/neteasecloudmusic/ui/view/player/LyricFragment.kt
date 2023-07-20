package com.hongyongfeng.neteasecloudmusic.ui.view.player

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class LyricFragment : BaseFragment<FragmentQrBinding, ViewModel>(
    FragmentQrBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentQrBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}