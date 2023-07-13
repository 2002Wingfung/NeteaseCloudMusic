package com.hongyongfeng.neteasecloudmusic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB:ViewBinding>(
    private val inflate:(LayoutInflater,ViewGroup?,Boolean)->VB
) :Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=inflate(inflater,container,false)
        initFragment(binding)
        return binding.root
    }

    abstract fun initFragment(binding: VB)

}