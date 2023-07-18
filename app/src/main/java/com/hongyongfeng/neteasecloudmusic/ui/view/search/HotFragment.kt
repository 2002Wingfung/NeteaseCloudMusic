package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentHotBinding
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.HotInterface
import com.hongyongfeng.neteasecloudmusic.model.Hot
import com.hongyongfeng.neteasecloudmusic.util.DisplayUtils
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.HotViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HotFragment : BaseFragment<FragmentHotBinding, HotViewModel>(
    FragmentHotBinding::inflate,
    HotViewModel::class.java,
    true
){
    private lateinit var flowLayout: FlowLayout
    private lateinit var binding: FragmentHotBinding
    private lateinit var mActivity: FragmentActivity
    private lateinit var publicViewModel: PublicViewModel
    private lateinit var viewModel: HotViewModel
    private var hotList = mutableListOf<Hot>()
    override fun initFragment(
        binding: FragmentHotBinding,
        viewModel: HotViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
        if (viewModel != null) {
            this.viewModel=viewModel
        }
        flowLayout = binding.layoutFlow.findViewById(R.id.flowlayout)
        flowLayout.setSpace(DisplayUtils.dp2px(15F), DisplayUtils.dp2px(15F))
        flowLayout.setPadding(
            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F),
            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F)
        )
        if (publicViewModel != null) {
            this.publicViewModel=publicViewModel
        }
    }

    override fun initListener() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity=requireActivity()
        if (hotList.isEmpty()){
            hotWordsRequest()
        }else{
            display(flowLayout, hotList)
        }

    }
    private fun hotWordsRequest(){
        publicViewModel!!.apply {
            getAPI(HotInterface::class.java).getHotData().getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> {
                            Log.e("TAGInternet",it.errMsg)
                            withContext(Dispatchers.Main){
                                Toast.makeText(mActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val list=it.response
                            hotList.addAll(list.data)
//                            for (bean in list.hot){
//                                println(bean.searchWord)
//                            }
                            display(flowLayout,list.data)

//                            println(list.data)
//                            println(list.code)
                            //切换主线程
                            //更新UI
                            //Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show()
                            //findNavController().navigate(R.id.action_loginFragment_to_mainNavFragment)
                        }
                    }
                }
            }
        }
    }
    private fun display(flowLayout: FlowLayout, hotList: List<Hot>) {
        for (hot in hotList) {
            //for (i in 0..10) {
            //新建一个TextView控件
            val tv = TextView(mActivity)
            //将网络请求中返回的字段设置在TextView中
            tv.text = hot.searchWord
            //tv.text = "123"
            //设置字体的大小
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            //设置字体居中
            tv.gravity = Gravity.CENTER
            //设置偏离左右边界的长度
            val paddingY = DisplayUtils.dp2px(6f)
            val paddingX = DisplayUtils.dp2px(6f)
            tv.setPadding(paddingX, paddingY, paddingX, paddingY)
            tv.isClickable = false
            //设置TextView为矩形
            val shape = GradientDrawable.RECTANGLE
            //设置圆角
            val radius = DisplayUtils.dp2px(14f)
            //设置边界的宽度
            val strokeWeight = DisplayUtils.dp2px(2f)
            //设置边界的颜色
            val stokeColor = resources.getColor(R.color.transparent)
            //设置默认(没按下时)的TextView样式
            val drawableDefault = GradientDrawable()
            drawableDefault.shape = shape
            drawableDefault.cornerRadius = radius.toFloat()
            drawableDefault.setStroke(strokeWeight, stokeColor)
            drawableDefault.setColor(ContextCompat.getColor(mActivity, R.color.white))
            //设置按下时TextView的样式
            val drawableChecked = GradientDrawable()
            drawableChecked.shape = shape
            drawableChecked.cornerRadius = radius.toFloat()
            drawableChecked.setColor(ContextCompat.getColor(mActivity, R.color.shallow_gray))
            //设置selector
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), drawableChecked)
            stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), drawableChecked)
            stateListDrawable.addState(intArrayOf(), drawableDefault)
            tv.background = stateListDrawable
            //设置点击事件
            tv.setOnClickListener { v: View? ->
                val key = tv.text.toString()
                viewModel.setText(key)
            }
            flowLayout.addView(tv)
        }
    }
}