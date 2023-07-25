package com.hongyongfeng.neteasecloudmusic.ui.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hongyongfeng.neteasecloudmusic.R

class SongsListViewHolder(
    itemView: View, val onClickListener:(view:View, position:Int)->Unit, deleteListener: ((view: View, position: Int) -> Unit)?,
    addListener: ((view: View, position: Int) -> Unit)?
):
    RecyclerView.ViewHolder(itemView) {
    var name:TextView?=null
    var title:TextView?=null
    var vip:TextView?=null
    var delete:TextView?=null
    var add:TextView?=null
    init {
        name=itemView.findViewById(R.id.tv_name)
        title=itemView.findViewById(R.id.tv_title)
        vip=itemView.findViewById(R.id.tv_vip)
        delete=itemView.findViewById(R.id.tv_delete)
        add=itemView.findViewById(R.id.tv_plus)
        itemView.setOnClickListener { view: View ->
            val position = adapterPosition
            //确保position值有效
            if (position != RecyclerView.NO_POSITION) {
                //点赞事件的监听回调
                onClickListener(view, position)
            }
        }
        delete?.setOnClickListener {
            val position = adapterPosition
            //确保position值有效
            if (position != RecyclerView.NO_POSITION) {
                //点赞事件的监听回调

                if (deleteListener != null) {
                    deleteListener(it, position)
                }
            }
        }
        add?.setOnClickListener {
            val position = adapterPosition
            //确保position值有效
            if (position != RecyclerView.NO_POSITION) {
                //点赞事件的监听回调
                if (addListener != null) {
                    addListener(it, position)
                }
            }
        }

    }
}