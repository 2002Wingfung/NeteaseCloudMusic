package com.hongyongfeng.neteasecloudmusic.adapter

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.PlayListViewHolder

class PlayListAdapter(private val list:List<PlayListBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun getItemCount(): Int=list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        holder as PlayListViewHolder
        val bean :PlayListBean= list[position]
        //println(holder.amount?.text)


        holder.title?.text = bean.getTitle()
        holder.amount?.text=bean.getAmount().toString()
        //println(holder.amount?.text)

    }
}