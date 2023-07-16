package com.hongyongfeng.neteasecloudmusic.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.adapter.PlayListAdapter
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding
import com.hongyongfeng.neteasecloudmusic.databinding.ItemSongsBinding

class SongsListViewHolder (itemView: View, val  onClickListener:(view:View, position:Int)->Unit, val binding: ItemSongsBinding):
    RecyclerView.ViewHolder(itemView) {
    var name:TextView?=null
    var title:TextView?=null
    init {
        name=itemView.findViewById(R.id.tv_name)
        title=itemView.findViewById(R.id.tv_title)

        itemView.setOnClickListener { view: View ->
            val position = adapterPosition
            //确保position值有效
            if (position != RecyclerView.NO_POSITION) {
                //点赞事件的监听回调
                onClickListener(view, position)
            }
        }
    }
}