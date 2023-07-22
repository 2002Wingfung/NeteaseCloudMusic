package com.hongyongfeng.neteasecloudmusic.ui.view.login

import android.content.Context
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentLoginBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class LoginFragment :BaseFragment<FragmentLoginBinding,ViewModel>(
    FragmentLoginBinding::inflate,
    null,
    true
){
    private lateinit var mActivity:FragmentActivity
    override fun initFragment(
        binding: FragmentLoginBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        mActivity=requireActivity()

        val prefs=mActivity.getSharedPreferences("player", Context.MODE_PRIVATE)
        prefs.edit {
            putLong("userId",1738181262)
            //到时候改成登录后返回的userId
        }
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }
}