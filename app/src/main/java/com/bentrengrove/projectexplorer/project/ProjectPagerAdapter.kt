package com.bentrengrove.projectexplorer.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.R
import io.noties.markwon.Markwon
import kotlinx.android.extensions.LayoutContainer

private val COLUMN_NODE_DIFF = object : DiffUtil.ItemCallback<ProjectQuery.Node>() {
    override fun areItemsTheSame(oldItem: ProjectQuery.Node, newItem: ProjectQuery.Node): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProjectQuery.Node, newItem: ProjectQuery.Node): Boolean {
        return oldItem == newItem
    }
}

class ProjectPagerAdapter(val markwon: Markwon, val onItemClick: (ProjectQuery.Node1)->Unit): ListAdapter<ProjectQuery.Node, ProjectPagerAdapter.ViewHolder>(COLUMN_NODE_DIFF) {
    inner class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        val adapter = ColumnAdapter(markwon, onItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.project_pager_view, parent, false)
        val viewHolder = ViewHolder(itemView)
        (itemView as RecyclerView).adapter = viewHolder.adapter
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.adapter.submitList(item.cards.nodes)
    }
}