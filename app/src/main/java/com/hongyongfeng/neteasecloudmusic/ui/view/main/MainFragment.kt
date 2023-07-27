package com.hongyongfeng.neteasecloudmusic.ui.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.PlayListAdapter
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentMainBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayList
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.PlayListInterface
import com.hongyongfeng.neteasecloudmusic.util.SetRecyclerView
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainFragment :BaseFragment<FragmentMainBinding,ViewModel>(
    FragmentMainBinding::inflate,
    null,
    true
){
    private lateinit var publicViewModel: PublicViewModel
    private lateinit var mActivity:FragmentActivity
    private lateinit var binding:FragmentMainBinding
    private lateinit var recyclerViewCollect:RecyclerView
    private lateinit var recyclerViewEstablish:RecyclerView
    private var listCollect= mutableListOf<PlayList>()
    private var listEstablish= mutableListOf<PlayList>()
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
    @SuppressLint("NotifyDataSetChanged")
    fun initPlayList(){
        publicViewModel!!.apply {
            getAPI(PlayListInterface::class.java).getPlayList("1738181262","50","0").getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAG",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(mActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val playList=it.response.playlist
                            val prefs=mActivity.getSharedPreferences("player", Context.MODE_PRIVATE)

                            val userId=prefs.getLong("userId",1738181262)
                            playList.forEach{
                                    list->
                                if (list.userId!=userId){
                                    listCollect.add(list)
                                    adapterCollect.notifyItemChanged(0)
                                }else{
                                    listEstablish.add(list)
                                    adapterEstablish.notifyItemChanged(0)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (listCollect.isEmpty()&&listEstablish.isEmpty()){
            initPlayList()
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
        if (publicViewModel != null) {
            this.publicViewModel=publicViewModel
        }
    }
    private fun initView(binding: FragmentMainBinding,dp:Int){
        val lp = binding.actionBar.layoutParams as LinearLayout.LayoutParams
        lp.topMargin= dp
        binding.actionBar.layoutParams = lp
        //根据id获取RecycleView的实例
        recyclerViewCollect = binding.rvCollect
        recyclerViewEstablish = binding.rvEstablish
    }
    private fun initListener(binding:FragmentMainBinding) {

        adapterCollect.setOnItemClickListener {
            view:View,position:Int->
            val bundle=Bundle()
            val collect=listCollect[position]
            bundle.putLong("id",collect.id)
            //Log.e("collect",collect.id.toString())
            bundle.putString("listName",collect.name)
            bundle.putString("url",collect.coverImgUrl)
            bundle.putString("creator",collect.creator.nickname)
            findNavController().navigate(R.id.action_mainFragment_to_listFragment,bundle)

        }
        adapterEstablish.setOnItemClickListener {
            view:View,position:Int->

            val bundle=Bundle()
            val establish=listEstablish[position]
            bundle.putLong("id",establish.id)
            bundle.putString("listName",establish.name)
            bundle.putString("url",establish.coverImgUrl)
            bundle.putString("creator",establish.creator.nickname)
            findNavController().navigate(R.id.action_mainFragment_to_listFragment,bundle)

            //println((view.findViewById(R.id.tv_title) as TextView).text)
        }
        binding.btnNva.setOnClickListener{
            mActivity.findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
            //println("success")
        }
        binding.layoutLikes.setOnClickListener {
            //findNavController().navigate(R.id.action_mainFragment_to_listFragment)

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