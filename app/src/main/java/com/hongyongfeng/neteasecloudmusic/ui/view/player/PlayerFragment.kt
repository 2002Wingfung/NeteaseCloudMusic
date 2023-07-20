package com.hongyongfeng.neteasecloudmusic.ui.view.player

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentPlayerBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding, ViewModel>(
    FragmentPlayerBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentPlayerBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}