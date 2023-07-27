package com.hongyongfeng.neteasecloudmusic.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R
import com.hongyongfeng.neteasecloudmusic.databinding.ItemListBinding

class PlayListViewHolder (itemView: View, val  onClickListener:(view:View, position:Int)->Unit, val binding: ItemListBinding):
    RecyclerView.ViewHolder(itemView) {
    var amount:TextView?=null
    var title:TextView?=null
    var img:ImageView?=null
    init {
        amount=itemView.findViewById(R.id.tv_amount)
        title=itemView.findViewById(R.id.tv_title)
        img=itemView.findViewById(R.id.img_album)
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