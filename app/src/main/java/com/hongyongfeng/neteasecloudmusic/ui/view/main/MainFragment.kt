package com.hongyongfeng.neteasecloudmusic.ui.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.PlayListAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentMainBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel

class MainFragment :BaseFragment<FragmentMainBinding,ViewModel>(
    FragmentMainBinding::inflate,
    null,
    true
){
    private lateinit var mActivity:FragmentActivity
    private lateinit var binding:FragmentMainBinding
    private lateinit var recyclerViewCollect:RecyclerView
    private lateinit var recyclerViewEstablish:RecyclerView
    private var listCollect= mutableListOf<PlayListBean>()
    private var listEstablish= mutableListOf<PlayListBean>()
    private var adapterCollect=PlayListAdapter(listCollect)
    private var adapterEstablish=PlayListAdapter(listEstablish)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initListener() {
        //TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (listCollect.size==0){
            listCollect.add(PlayListBean("123",4))
        }
        if (listEstablish.size==0){
            listEstablish.add(PlayListBean("456",7))
        }

        //adapterEstablish.notifyDataSetChanged()
        //adapterCollect.notifyDataSetChanged()
        SetRecyclerView.setRecyclerViewScroll(
            mActivity,
            recyclerViewCollect,
            adapterCollect
        )
        SetRecyclerView.setRecyclerViewScroll(
            mActivity,
            recyclerViewEstablish,
            adapterEstablish
        )
        val nav: NavigationView =binding.navView
        nav.layoutParams.width=getResources().getDisplayMetrics().widthPixels *4/ 5;//屏幕的三分之一
        nav.setLayoutParams(nav.layoutParams);
        val headerLayout =nav.inflateHeaderView(R.layout.nav_header)
        headerLayout.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
        nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 ->{
                    Toast.makeText(activity, "我的消息", Toast.LENGTH_SHORT).show();
                }
                R.id.item2 -> {
                    Toast.makeText(activity, "设置", Toast.LENGTH_SHORT).show()
                }
                R.id.item3 -> {
                    Toast.makeText(activity, "深色模式", Toast.LENGTH_SHORT).show()
                }
                R.id.item4 ->{
                    Toast.makeText(activity, "关于", Toast.LENGTH_SHORT).show();
                }
                R.id.item5 -> {
                    Toast.makeText(activity, "退出登录", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
    }
    override fun initFragment(
        binding: FragmentMainBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        mActivity=requireActivity()
        this.binding=binding
        initView(binding, StatusBarUtils.getStatusBarHeight(activity as AppCompatActivity)+10)
        initListener(binding)
    }
    private fun initView(binding: FragmentMainBinding,dp:Int){
        val lp = binding.actionBar.getLayoutParams() as LinearLayout.LayoutParams
        lp.topMargin= dp
        binding.actionBar.setLayoutParams(lp)
        //根据id获取RecycleView的实例
        recyclerViewCollect = binding.rvCollect
        recyclerViewEstablish = binding.rvEstablish


    }
    private fun initListener(binding:FragmentMainBinding) {
        adapterCollect.setOnItemClickListener {
            view:View,position:Int->
//            position.showToast(activity)
//            (view.findViewById(R.id.tv_title) as TextView).text="niubi"
//            println((view.findViewById(R.id.tv_title) as TextView).text)

        }
        adapterEstablish.setOnItemClickListener {
            view:View,position:Int->
////            position.showToast(activity)
//            (view.findViewById(R.id.tv_title) as TextView).text="niubi"

            println((view.findViewById(R.id.tv_title) as TextView).text)
        }
        binding.btnNva.setOnClickListener{
            binding.drawerLayout.openDrawer(GravityCompat.START)
            println("success")
        }
        binding.layoutLikes.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_listFragment)

        }
        binding.layoutRecently.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_recentlyFragment)

        }
        binding.btnQrcode.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_QrFragment)
        }
        binding.searchBar.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)

        }
    }
}