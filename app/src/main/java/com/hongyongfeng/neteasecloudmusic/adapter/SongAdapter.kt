package com.hongyongfeng.neteasecloudmusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.model.entity.Song
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.SongsListViewHolder

class SongAdapter(private val list:List<Song>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var method:(view:View,position:Int)->Unit
    private lateinit var delete:(view:View,position:Int)->Unit
    private lateinit var add:(view:View,position:Int)->Unit

    fun setOnItemClickListener(method:(view:View,position:Int)->Unit,delete:(view:View,position:Int)->Unit,add:(view:View,position:Int)->Unit){
        this.method=method
        this.delete=delete
        this.add=add
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_songs, parent, false)
        return SongsListViewHolder(view,method,delete,add)
    }

    override fun getItemCount(): Int=list.size

    override fun getItemViewType(position: Int): Int {
        return position
        // 给每个ItemView指定不同的类型，这样在RecyclerView看来，这些ItemView全是不同的，不能复用
        //这样就避免了滑动时复用出现数据错乱
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        if (list.isNotEmpty()&&position<list.size){
            holder as SongsListViewHolder
            val bean :Song= list[position]
            holder.title?.text = bean.artist
            holder.name?.text=bean.name
            holder.vip?.visibility=View.GONE
            holder.delete?.visibility=View.VISIBLE
            holder.add?.visibility=View.GONE
        }
    }
}