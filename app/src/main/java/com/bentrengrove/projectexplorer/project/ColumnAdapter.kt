package com.bentrengrove.projectexplorer.project

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.R
import io.noties.markwon.Markwon
import kotlinx.android.extensions.LayoutContainer
import java.lang.IllegalStateException

private val CARD_DIFF = object : DiffUtil.ItemCallback<ProjectQuery.Node1>() {
    override fun areItemsTheSame(oldItem: ProjectQuery.Node1, newItem: ProjectQuery.Node1): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProjectQuery.Node1, newItem: ProjectQuery.Node1): Boolean {
        return oldItem == newItem
    }
}

class ColumnAdapter(val markwon: Markwon, val onItemClick: (ProjectQuery.Node1)->Unit): ListAdapter<ProjectQuery.Node1, RecyclerView.ViewHolder>(CARD_DIFF) {
    inner class NoteViewHolder(val containerView: View): RecyclerView.ViewHolder(containerView) {
        val lblBody = containerView.findViewById<TextView>(R.id.lblBody)
        val lblFooter = containerView.findViewById<TextView>(R.id.lblFooter)

        fun bind(item: ProjectQuery.Node1) {
            markwon.setMarkdown(lblBody, item.note ?: "")
            lblFooter.text = "Added by ${item.creator?.login ?: "ghost"}"
        }
    }

    class IssueViewHolder(val containerView: View): RecyclerView.ViewHolder(containerView) {
        val lblBody = containerView.findViewById<TextView>(R.id.lblBody)
        val lblFooter = containerView.findViewById<TextView>(R.id.lblFooter)
        val imgIcon = containerView.findViewById<ImageView>(R.id.imgIcon)

        fun bind(item: ProjectQuery.Node1) {
            val issue = item.content?.asIssue ?: return
            lblBody.text = issue.title
            lblFooter.text = "Added by ${item.creator?.login ?: "ghost"}"
            if (issue.closed) {
                imgIcon.setImageResource(R.drawable.ic_done_black_24dp)
                imgIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(containerView.context, R.color.issue_closed))
            } else {
                imgIcon.setImageResource(R.drawable.ic_error_outline_black_24dp)
                imgIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(containerView.context, R.color.issue_open))
            }
        }
    }

    class PullRequestViewHolder(val containerView: View): RecyclerView.ViewHolder(containerView) {
        val lblBody = containerView.findViewById<TextView>(R.id.lblBody)
        val lblFooter = containerView.findViewById<TextView>(R.id.lblFooter)

        fun bind(item: ProjectQuery.Node1) {
            val pr = item.content?.asPullRequest ?: return
            lblBody.text = pr.title
            lblFooter.text = "Added by ${item.creator?.login ?: "ghost"}"
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when {
            item.content?.asIssue != null -> {
                R.layout.card_issue_viewholder
            }
            item.content?.asPullRequest != null -> {
                R.layout.card_pr_viewholder
            }
            else -> {
                R.layout.card_note_viewholder
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.card_issue_viewholder -> IssueViewHolder(itemView)
            R.layout.card_note_viewholder -> NoteViewHolder(itemView)
            R.layout.card_pr_viewholder -> PullRequestViewHolder(itemView)
            else -> throw IllegalStateException("Unknown view type in ColumnAdapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is NoteViewHolder -> holder.bind(item)
            is IssueViewHolder -> holder.bind(item)
            is PullRequestViewHolder -> holder.bind(item)
            else -> throw IllegalStateException("Unknown view holder in ColumnAdapter: ${holder::class.java.canonicalName}")
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}