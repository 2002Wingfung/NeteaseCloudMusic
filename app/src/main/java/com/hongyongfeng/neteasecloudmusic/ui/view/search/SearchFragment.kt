package com.hongyongfeng.neteasecloudmusic.ui.view.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.base.BaseFragment
import com.hongyongfeng.neteasecloudmusic.databinding.FragmentSearchBinding
import com.hongyongfeng.neteasecloudmusic.util.KeyboardUtils
import com.hongyongfeng.neteasecloudmusic.util.StatusBarUtils
import com.hongyongfeng.neteasecloudmusic.util.showToast
import com.hongyongfeng.neteasecloudmusic.viewmodel.HotViewModel
import com.hongyongfeng.neteasecloudmusic.viewmodel.PublicViewModel


class SearchFragment: BaseFragment<FragmentSearchBinding, HotViewModel>(
    FragmentSearchBinding::inflate,
    HotViewModel::class.java,
    true
){
    private lateinit var mActivity: FragmentActivity
    private lateinit var binding:FragmentSearchBinding
    private lateinit var edtSearch:EditText
    private lateinit var publicViewModel: PublicViewModel
    private lateinit var viewModel: HotViewModel
    private var count=0
    private var num=1
    override fun onResume() {
        super.onResume()
        //println(456)
        val edt=binding.edtSearch
        if (edt.text.toString()!=""){
            //
            if (count==1){
                edt.setText("")
                count=0
            }
        }
        edt.isFocusable = true
        edt.isFocusableInTouchMode = true
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        edt.requestFocus()
        edt.findFocus()
        //
    }

    override fun initFragment(
        binding: FragmentSearchBinding,
        viewModel: HotViewModel?,
        publicViewModel: PublicViewModel?,
        savedInstanceState: Bundle?
    ) {
        this.binding=binding

        if (viewModel != null) {
            this.viewModel=viewModel
        }
        if (publicViewModel != null) {
            this.publicViewModel=publicViewModel
        }
        edtSearch=binding.edtSearch

        count=1
        if (!this::mActivity.isInitialized){
            mActivity=requireActivity()
            this.viewModel.editText.observe(mActivity, Observer {
                    editText->
                binding.edtSearch.setText(editText)
                println(editText.toString())
                search(editText)
                //退出SearchFragment之后，重新进入，他又会执行这个方法，应该是和重走生命周期有关
            })
        }
    }


    private fun search(text:String){
        //text.showToast(mActivity)
        if (edtSearch.text.toString()!=text){
            edtSearch.setText(text)
        }
        //println(R.id.hotFragment)
        if (num==0){
        }else{
            println(num)
            num=0

        }
        val bundle=Bundle()
        bundle.putString("text",text)
        //println(controller.currentDestination?.id)
        try {
            childFragmentManager.fragments[0].apply{
                childFragmentManager.fragments.forEach{
                    println(it)
                    if (it is HotFragment){
                        if (it.isVisible){
                            //mActivity.findNavController(R.id.search_nav).navigate(R.id.resultFragment,bundle,NavOptions.Builder().setPopUpTo(R.id.hotFragment,true).build())
                            mActivity.findNavController(R.id.search_nav).navigate(R.id.action_hotFragment_to_resultFragment,bundle)
                        }
                    }
                }
            }

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

//        appService2.getResponseBody().enqueue(object : Callback<ResponseBody> {
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
                activity!!.supportFragmentManager.popBackStack()
            }
        }


        binding.btnBack.setOnClickListener {
            //
            edtSearch.setText("")
            activity!!.supportFragmentManager.popBackStack()

            childFragmentManager.fragments[0].apply{
                for (fragment in childFragmentManager.fragments){
                    if (fragment is HotFragment) {
                        try {
                            activity!!.supportFragmentManager.popBackStack()
                        }catch (e:Exception){
                            println(e)
                        }
                    }

                }
            }
        }
        edtSearch.apply {
            setOnFocusChangeListener {
                view,hasFocus->
                if (hasFocus){

                }else{
                    KeyboardUtils.hideKeyboardWithQuery(edtSearch)
                }
            }
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
                        try {
                            childFragmentManager.fragments[0].apply{
                                childFragmentManager.fragments.forEach{
                                    //println(it)
                                    if (ResultFragment::class.java.isAssignableFrom(it.javaClass)){
                                        if (it.isVisible){
                                            //mActivity.findNavController(R.id.search_nav).navigate(R.id.action_resultFragment_to_hotFragment)
                                            mActivity.findNavController(R.id.search_nav).navigate(R.id.hotFragment,null,NavOptions.Builder().setPopUpTo(R.id.resultFragment,true).build())
                                        }
                                    }
                                }
                            }

                            //mActivity.findNavController(R.id.search_nav).navigate(R.id.action_resultFragment_to_hotFragment)


                            //mActivity.findNavController(R.id.search_nav)
//                        for (fragment in childFragmentManager.fragments){
//                            if (fragment is HotFragment){
//                                if (fragment.isResumed){
//
//                                }else{
//                                    mActivity.findNavController(R.id.search_nav).navigate(R.id.action_resultFragment_to_hotFragment)
//
//                                }
//                                break
//                            }else{
//                                println(fragment)
//
//                            }
//                        }
                            //看看如何才能获取到第一次的NavController，这样才可能不会抛出异常
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    } else {
                        binding.imgClear.visibility = View.INVISIBLE

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