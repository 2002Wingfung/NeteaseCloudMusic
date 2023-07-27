package com.hongyongfeng.neteasecloudmusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.model.Songs
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.LoadMoreViewHolder
import com.hongyongfeng.neteasecloudmusic.ui.viewholder.SongsListViewHolder

class SongsAdapter(private val list:List<Songs>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var method:(view:View,position:Int)->Unit
    private lateinit var add:(view:View,position:Int)->Unit
    fun setOnItemClickListener(method:(view:View,position:Int)->Unit,add:(view:View,position:Int)->Unit){
        this.method=method
        this.add=add
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType!=-1){
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_songs, parent, false)
            SongsListViewHolder(view,method,null,add)
        }else LoadMoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
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
            val bean :Songs= list[position]
            //println(holder.amount?.text)
            val artists=java.lang.StringBuilder()
            val artistList=bean.getArtists()
            for (artist in artistList!!){
                if (artist.equals(artistList.get(artistList.size-1))){
                    artists.append(artist.name)
                }else{
                    artists.append(artist.name).append("/")
                }
            }
            artists.append(" - ").append(bean.getAlbum()!!.name)
            holder.title?.text = artists
            holder.name?.text=bean.name
            val fee=bean.fee
            if (fee==0 ||fee==8) {
                holder.vip?.visibility=View.GONE
            }
        }
    }
}