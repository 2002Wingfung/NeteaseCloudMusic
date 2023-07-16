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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentSearchBinding
import com.hongyongfeng.neteasecloudmusic.network.APIResponse
import com.hongyongfeng.neteasecloudmusic.network.api.AppService
import com.hongyongfeng.neteasecloudmusic.network.api.Search
import com.hongyongfeng.neteasecloudmusic.network.res.Hot
import com.hongyongfeng.neteasecloudmusic.util.DisplayUtils
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DriverManager.println

class SearchFragment: BaseFragment<FragmentSearchBinding, SearchViewModel>(
    FragmentSearchBinding::inflate,
    SearchViewModel::class.java,
    true
){
//    private lateinit var flowLayout: FlowLayout
//    private lateinit var flowLayout: FlowLayout
    private lateinit var mActivity: FragmentActivity
    private lateinit var binding:FragmentSearchBinding
    private lateinit var edtSearch:EditText
    private lateinit var publicViewModel: PublicViewModel
    private lateinit var viewModel: SearchViewModel

    override fun initFragment(
        binding: FragmentSearchBinding,
        viewModel: SearchViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding
//        flowLayout = binding.layoutFlow.findViewById(R.id.flowlayout)
//        flowLayout.setSpace(DisplayUtils.dp2px(15F), DisplayUtils.dp2px(15F))
//        flowLayout.setPadding(
//            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F),
//            DisplayUtils.dp2px(5F), DisplayUtils.dp2px(5F)
//        )
        if (viewModel != null) {
            this.viewModel=viewModel
        }
        if (publicViewModel != null) {
            this.publicViewModel=publicViewModel
        }
        edtSearch=binding.edtSearch
        mActivity=requireActivity()

        this.viewModel.editText.observe(mActivity, Observer {
                editText->
            edtSearch.setText(editText)
            search(editText)
        })


    }


    fun search(text:String){
        text.showToast(mActivity)
        edtSearch.setText(text)

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
            if (text!=""){
                search(text)
            }else{
                "还没有输入关键词喔!".showToast(mActivity)
            }
        }
        binding.btnBack.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
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

        val lp = binding.actionBar.layoutParams as ConstraintLayout.LayoutParams
        lp.topMargin= StatusBarUtils.getStatusBarHeight(mActivity as AppCompatActivity)+10
        binding.actionBar.setLayoutParams(lp)
//        hotWordsRequest()
    }
//    private fun hotWordsRequest(){
//        publicViewModel!!.apply {
//            getAPI(Search::class.java).getHotData().getResponse {
//                    flow ->
//                flow.collect(){
//                    when(it){
//                        is APIResponse.Error-> Log.e("TAG",it.errMsg)
//                        is APIResponse.Loading-> Log.e("TAG","loading")
//                        is APIResponse.Success-> withContext(Dispatchers.Main){
//                            val list=it.response
////                            for (bean in list.hot){
////                                println(bean.searchWord)
////                            }
//                            display(flowLayout,list.data)
//
////                            println(list.data)
////                            println(list.code)
//                            //切换主线程
//                            //更新UI
//                            //Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show()
//                            //findNavController().navigate(R.id.action_loginFragment_to_mainNavFragment)
//                        }
//                    }
//                }
//            }
//        }
//    }

}