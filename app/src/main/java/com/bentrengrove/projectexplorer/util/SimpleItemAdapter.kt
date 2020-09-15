package com.bentrengrove.projectexplorer.util

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.projectexplorer.R
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer

interface SimpleItem {
    val id: String
    val title: String
    val subtitle: String?
    val imageUri: Uri?

    override fun equals(other: Any?): Boolean
}

class SimpleItemAdapter<T: SimpleItem>(val onItemClick: (Int, T)->Unit): ListAdapter<T, SimpleItemAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    })
{
    class ViewHolder(val containerView: View): RecyclerView.ViewHolder(containerView) {
        val lblText1: TextView = containerView.findViewById(R.id.lblText1)
        val lblText2: TextView = containerView.findViewById(R.id.lblText2)
        val imgIcon: ImageView = containerView.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.lblText1.text = item.title
        holder.lblText2.text = item.subtitle
        if (item.imageUri != null) {
            Picasso.get().load(item.imageUri).into(holder.imgIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(position, item as T) }
    }
}