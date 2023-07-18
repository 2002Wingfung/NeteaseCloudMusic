package com.hongyongfeng.neteasecloudmusic.ui.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.base.BaseActivity
import com.hongyongfeng.neteasecloudmusic.databinding.ActivityPlayerBinding
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentMainBinding
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast


class PlayerActivity :BaseActivity<ActivityPlayerBinding,ViewModel>(
    ActivityPlayerBinding::inflate
){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = getIntent().getExtras();
        binding.tvPlayer.text=bundle?.getString("name")
        binding.tvId.text=bundle?.getInt("id").toString()
        transparentNavBar(this)
        initView(binding, StatusBarUtils.getStatusBarHeight(this as AppCompatActivity)+5)

        //binding.tvTitle.textContainerInset = UIEdgeInsets.init(top: 0, left:-lineFragmentPadding , bottom: 0, right: -lineFragmentPadding )
        initListener()
    }
    private fun initView(binding: ActivityPlayerBinding, dp:Int){
        val lp = binding.layoutActionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= dp
        binding.layoutActionBar.layoutParams = lp
        binding.tvTitle.findFocus()
        binding.tvTitle.requestFocus()
        binding.tvSinger.findFocus()
        binding.tvSinger.requestFocus()
    }
    private fun initListener() {
        binding.icPlay.setOnClickListener {
            "play".showToast(this)
        }
        binding.icNext.setOnClickListener {
            "next".showToast(this)

        }
        binding.icBack.setOnClickListener {
            "back".showToast(this)

        }
        binding.icMode.setOnClickListener {
            "mode".showToast(this)

        }
        binding.icList.setOnClickListener {
            "list".showToast(this)
        }
    }

}