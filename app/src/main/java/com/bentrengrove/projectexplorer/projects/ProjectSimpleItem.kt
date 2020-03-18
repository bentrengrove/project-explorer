package com.bentrengrove.projectexplorer.projects

import android.net.Uri
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.projectexplorer.util.SimpleItem

data class ProjectSimpleItem(
    override val id: String,
    override val title: String,
    override val subtitle: String?,
    override val imageUri: Uri?,
    val number: Int
) : SimpleItem

fun ProjectsQuery.Node.toSimpleItem(): ProjectSimpleItem {
    return ProjectSimpleItem(id, name, this.number.toString(), null, this.number)
}