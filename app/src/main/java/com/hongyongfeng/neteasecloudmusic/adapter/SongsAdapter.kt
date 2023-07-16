package com.hongyongfeng.neteasecloudmusic.adapter

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding
import com.hongyongfeng.neteasecloudmusic.databinding.ItemSongsBinding
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.PlayListViewHolder
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.SongsListViewHolder

class SongsAdapter(private val list:List<Songs>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var method:(view:View,position:Int)->Unit
    fun setOnItemClickListener(method:(view:View,position:Int)->Unit){
        this.method=method
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_songs, parent, false)
        //println((view.findViewById(R.id.tv_title) as TextView).text)
        val binding = ItemSongsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongsListViewHolder(view,method,binding)
    }

    override fun getItemCount(): Int=list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        holder as SongsListViewHolder
        val bean :Songs= list[position]
        //println(holder.amount?.text)


        //holder.title?.text = bean.getTitle()
        holder.name?.text=bean.getName()
        //println(holder.amount?.text)

    }
}