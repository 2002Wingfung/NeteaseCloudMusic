package com.hongyongfeng.neteasecloudmusic.adapter

import android.content.ClipData.Item
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayList
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.PlayListViewHolder
import com.hongyongfeng.neteasecloudmusic.util.MyApplication
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class PlayListAdapter(private val list:List<PlayList>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var method:(view:View,position:Int)->Unit
    fun setOnItemClickListener(method:(view:View,position:Int)->Unit){
        this.method=method
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        //println((view.findViewById(R.id.tv_title) as TextView).text)
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayListViewHolder(view,method,binding)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int=list.size
    private val prefs: SharedPreferences =MyApplication.context.getSharedPreferences("player", Context.MODE_PRIVATE)
    private var id :Long=-1
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        id=prefs.getLong("userId",1738181262)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        holder as PlayListViewHolder
        val bean :PlayList= list[position]
        var description=""
        holder.title?.text = bean.name
        description = if (bean.userId==id){
            bean.trackCount.toString()+"首"
        }else{
            bean.trackCount.toString()+"首,by ${bean.creator.nickname}"
        }

        holder.amount?.text=description
        Picasso.get().load(bean.coverImgUrl).fit().into(holder.img)


    }
}