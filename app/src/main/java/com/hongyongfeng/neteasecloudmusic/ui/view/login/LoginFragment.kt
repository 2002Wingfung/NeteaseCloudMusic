package com.hongyongfeng.neteasecloudmusic.ui.view.login

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentLoginBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class LoginFragment :BaseFragment<FragmentLoginBinding,ViewModel>(
    FragmentLoginBinding::inflate,
    null,
    true
){
    override fun initFragment(
        binding: FragmentLoginBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
    }
}