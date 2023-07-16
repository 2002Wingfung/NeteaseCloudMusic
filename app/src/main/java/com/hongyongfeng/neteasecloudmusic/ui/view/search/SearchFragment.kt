package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentSearchBinding
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.SearchViewModel

class SearchFragment: BaseFragment<FragmentSearchBinding, SearchViewModel>(
    FragmentSearchBinding::inflate,
    SearchViewModel::class.java,
    true
){

    private lateinit var mActivity: FragmentActivity
    private lateinit var binding:FragmentSearchBinding
    private lateinit var edtSearch:EditText
    private lateinit var publicViewModel: PublicViewModel
    private lateinit var viewModel: SearchViewModel

    private var controller: NavController?=null
    private var num=1
    override fun onResume() {
        super.onResume()
        //println(456)
        if (binding.edtSearch.text.toString()!=""){
            binding.edtSearch.setText("")
        }
        //

    }
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

        if (!this::mActivity.isInitialized){
            println(123)
            mActivity=requireActivity()
            this.viewModel.editText.observe(mActivity, Observer {
                    editText->
                binding.edtSearch.setText(editText)
                search(editText)
            })
        }





    }


    fun search(text:String){
        //text.showToast(mActivity)
        binding.edtSearch.setText(text)
        println(R.id.hotFragment)
        if (num==0){
        }else{
            println(num)
            num=0

        }
        val bundle=Bundle()
        bundle.putString("text",text)
        //println(controller.currentDestination?.id)
        try {
            mActivity.findNavController(R.id.search_nav).navigate(R.id.action_hotFragment_to_resultFragment,bundle)

        }catch (e:Exception){
            e.printStackTrace()
        }

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
        binding.imgClear.setOnClickListener {
            val edt=binding.edtSearch
            if (edt.text.toString()!=""){
                edt.setText("")
            }
        }


        binding.btnBack.setOnClickListener {
            binding.edtSearch.setText("请输入关键词")
            activity!!.supportFragmentManager.popBackStack()
//            view?.let { it1 -> Navigation.findNavController(it1).navigateUp() };
        }
        edtSearch.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    if (this@apply.text.toString().isNotEmpty()) {
                        binding.imgClear.visibility = View.VISIBLE
                    } else {
                        binding.imgClear.visibility = View.INVISIBLE
                    }
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (this@apply.text.toString().isNotEmpty()) {
                        binding.imgClear.visibility = View.VISIBLE
                    } else {
                        binding.imgClear.visibility = View.INVISIBLE
                        try {
                            mActivity.findNavController(R.id.search_nav).navigate(R.id.action_resultFragment_to_hotFragment)
                            //看看如何才能获取到第一次的NavController，这样才可能不会抛出异常
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            })
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