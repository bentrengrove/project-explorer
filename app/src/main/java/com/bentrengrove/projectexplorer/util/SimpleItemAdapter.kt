package com.bentrengrove.projectexplorer.util

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.projectexplorer.R
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.simple_list_item.view.*

interface SimpleItem {
    val id: String
    val title: String
    val subtitle: String?
    val imageUri: Uri?

    override fun equals(other: Any?): Boolean
}

class SimpleItemAdapter<T: SimpleItem>(val onItemClick: (T)->Unit): ListAdapter<T, SimpleItemAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    })
{
    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.lblText1.text = item.title
        holder.itemView.lblText2.text = item.subtitle
        if (item.imageUri != null) {
            Picasso.get().load(item.imageUri).into(holder.itemView.imgIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(item as T) }
    }
}