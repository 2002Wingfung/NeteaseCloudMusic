package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentSearchBinding
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.AppService
import com.hongyongfeng.neteasecloudmusic.network.res.Hot
import com.hongyongfeng.neteasecloudmusic.util.DisplayUtils
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DriverManager.println

class SearchFragment: BaseFragment<FragmentSearchBinding, ViewModel>(
    FragmentSearchBinding::inflate,
    null,
    true
){
    private lateinit var flowLayout: FlowLayout
//    private lateinit var flowLayout: FlowLayout
    private lateinit var mActivity: FragmentActivity
    private lateinit var binding:FragmentSearchBinding
    private lateinit var edtSearch:EditText
    private lateinit var publicViewModel: PublicViewModel

    override fun initFragment(
        binding: FragmentSearchBinding,
        viewModel: ViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        flowLayout = binding.layoutFlow.findViewById(R.id.flowlayout)
        this.binding=binding
        flowLayout.setSpace(DisplayUtils.dp2px(15F), DisplayUtils.dp2px(15F))
        flowLayout.setPadding(
            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F),
            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F)
        )

        if (publicViewModel != null) {
            this.publicViewModel=publicViewModel
        }
        edtSearch=binding.edtSearch
    }

    fun search(text:String){
        text.showToast(mActivity)


//        val appService2=ServiceCreator.create1<AppService>()
//        appService2.getAppData().enqueue(object : Callback<List<Hot>> {
//            override fun onResponse(call: Call<List<Hot>>, response: Response<List<Hot>>) {
//                val list=response.body()
//                if (list!=null){
//                    for (app in list){
//                        Log.d("MainActivity","searchWord is ${app.searchWord}")
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Hot>>, t: Throwable) {
//                t.printStackTrace()
//            }
//        })

//        appService2.getResponseBody().enqueue(object : Callback<ResponseBody>{
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                try {
//                    //把原始数据转为字符串
//
//                    //把原始数据转为字符串
//                    val jsonStr = String(response.body()!!.bytes())
//                    Log.e("retrofit获取到的数据", jsonStr)
//
//                    //jsonToObj(jsonStr) //这是对字符串数据解析具体数据方法
//
//
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                //TODO("Not yet implemented")
//            }
//
//        })
    }
    override fun initListener() {
        binding.btnSearch.setOnClickListener {
            val text=binding.edtSearch.text.toString()
            if (text!=null&&text!=""){
                search(text)
            }else{
                "还没有输入关键词喔!".showToast(mActivity)
            }
        }
        edtSearch.apply {
            setOnEditorActionListener{
                    v,actionId,event->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val text=this.text?.toString()
                    if (text!=null&&text!=""){
                        search(text)
                    }else{
                        "还没有输入关键词喔!".showToast(mActivity)
                    }
                }
                false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity=requireActivity()

        val lp = binding.actionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= StatusBarUtils.getStatusBarHeight(mActivity as AppCompatActivity)+10
        binding.actionBar.setLayoutParams(lp)
        hotWordsRequest()
    }
    fun hotWordsRequest(){
        publicViewModel!!.apply {
            getAPI(AppService::class.java).getAppData().getResponse {
                    flow ->
                flow.collect(){
                    when(it){
                        is APIResponse.Error-> Log.e("TAG",it.errMsg)
                        is APIResponse.Loading-> Log.e("TAG","loading")
                        is APIResponse.Success-> withContext(Dispatchers.Main){
                            val list=it.response
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
                edtSearch.setText(key)
                search(key)

//                listener.sendValue(key)
                println(key)
            }
            flowLayout.addView(tv)
        }
    }
}