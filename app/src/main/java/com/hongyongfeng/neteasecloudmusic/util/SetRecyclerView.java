package com.hongyongfeng.neteasecloudmusic.util;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置RecyclerView
 * @author Wingfung Hung
 */
public class SetRecyclerView {
    public static void setRecyclerViewScroll(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false) {
            @Override
            public boolean canScrollVertically() {
                // 直接禁止垂直滑动
                return false;
            }
        };
        recyclerView(layoutManager,context,recyclerView,adapter);
    }
    public static void recyclerView(LinearLayoutManager layoutManager, Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter){
        //将LinearLayoutManager实例传入RecycleView的实例中，设置RecycleView的item布局
        recyclerView.setLayoutManager(layoutManager);
        //通过设置ItemDecoration 来装饰Item的效果,设置间隔线
//        DividerItemDecoration mDivider = new
//                DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(mDivider);
        //将adapter传入recyclerView对象中
        recyclerView.setAdapter(adapter);
        //添加默认动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //使recyclerView滚动到0索引的位置
        recyclerView.scrollToPosition(0);
    }
    public static void setRecyclerView(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        //获取LinearLayoutManager实例，设置布局方式,默认为可以垂直滚动
        LinearLayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView(layoutManager,context,recyclerView,adapter);
    }
}
