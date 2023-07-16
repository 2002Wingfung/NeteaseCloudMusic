package com.hongyongfeng.neteasecloudmusic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

abstract class BaseFragment<VB:ViewBinding,VM:ViewModel>(
    private val inflate:(LayoutInflater,ViewGroup?,Boolean)->VB,
    private val viewModelClass:Class<VM>?,
    private val publicViewModelTag:Boolean=false
) :Fragment(){
    private val viewModel by lazy {//一个页面一个ViewModel
        val viewModelProvider=ViewModelProvider(requireActivity())

        viewModelClass?.let{//有时候不是每个页面都有ViewModel，可能数据量很少的时候就不需要ViewModel了
            viewModelProvider[it]
            //上面这行代码等价于：
            //viewModelProvider.get(it)
        }
    }
    private val publicViewModel: PublicViewModel? by lazy{
        if (publicViewModelTag){
            ViewModelProvider(requireActivity())[PublicViewModel::class.java]
        }else{
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=inflate(inflater,container,false)
        initFragment(binding,viewModel,publicViewModel,savedInstanceState)
        initListener()
        return binding.root
    }

    abstract fun initListener()

    abstract fun initFragment(binding: VB,viewModel:VM?,publicViewModel: PublicViewModel?,savedInstanceState: Bundle?)

}