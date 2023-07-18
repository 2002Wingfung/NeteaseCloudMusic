package com.hongyongfeng.neteasecloudmusic.ui.app

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityPlayerBinding

class PlayerActivity :BaseActivity<ActivityPlayerBinding,ViewModel>(
    ActivityPlayerBinding::inflate
){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getIntent().getExtras();
        binding.tvPlayer.text=bundle?.getString("name")
        binding.tvId.text=bundle?.getInt("id").toString()


    }
}