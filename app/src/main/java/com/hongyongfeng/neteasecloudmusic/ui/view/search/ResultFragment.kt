package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentResultBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class ResultFragment : BaseFragment<FragmentResultBinding, ViewModel>(
    FragmentResultBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentResultBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}