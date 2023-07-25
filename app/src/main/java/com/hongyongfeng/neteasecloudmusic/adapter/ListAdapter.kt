package com.hongyongfeng.neteasecloudmusic.adapter

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding
import com.hongyongfeng.neteasecloudmusic.databinding.ItemSongsBinding
import com.hongyongfeng.neteasecloudmusic.model.Detail
import com.hongyongfeng.neteasecloudmusic.model.PlayListBean
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.LoadMoreViewHolder
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.PlayListViewHolder
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.SongsListViewHolder

class ListAdapter(private val list:List<Detail>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var method:(view:View,position:Int)->Unit
    fun setOnItemClickListener(method:(view:View,position:Int)->Unit){
        this.method=method
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType!=-1){
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_songs, parent, false)
            //println((view.findViewById(R.id.tv_title) as TextView).text)
            val binding = ItemSongsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SongsListViewHolder(view,method,binding)
        }else return LoadMoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))

    }

    override fun getItemCount(): Int=list.size+1

    override fun getItemViewType(position: Int): Int {
        return if (position==list.size){
            -1
        }else{
            position
        }
        // 给每个ItemView指定不同的类型，这样在RecyclerView看来，这些ItemView全是不同的，不能复用
        //这样就避免了滑动时复用出现数据错乱
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder , position: Int) {
        if (list.isNotEmpty()&&position<list.size){
            holder as SongsListViewHolder
            val bean :Detail= list[position]
            //println(holder.amount?.text)
            val artists=java.lang.StringBuilder()
            val artistList=bean.ar
            for (artist in artistList){
                if (artist == artistList[artistList.size-1]){
                    artists.append(artist.name)
                }else{
                    artists.append(artist.name).append("/")
                }
            }
            artists.append(" - ").append(bean.al.name)
            holder.title?.text = artists
            holder.name?.text=bean.name
            holder.vip?.visibility=View.GONE
        }

    }
}