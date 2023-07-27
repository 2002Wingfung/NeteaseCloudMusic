package com.hongyongfeng.neteasecloudmusic.ui.view.qr

import android.os.Bundle
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentQrBinding
import com.hongyongfeng.neteasecloudmusic.testNew.TestViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class QrFragment : BaseFragment<FragmentQrBinding, TestViewModel>(//到时候做的时候改过来
    FragmentQrBinding::inflate,
    TestViewModel::class.java,//到时候做的时候改过来
    true
){
    override fun initFragment(
        binding: FragmentQrBinding,
        viewModel: TestViewModel?,//到时候做的时候改过来
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        viewModel?.getData()
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}