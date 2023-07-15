package com.hongyongfeng.neteasecloudmusic.ui.view.list

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentListBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentLoginBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentSearchBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class ListFragment: BaseFragment<FragmentListBinding, ViewModel>(
    FragmentListBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentListBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}