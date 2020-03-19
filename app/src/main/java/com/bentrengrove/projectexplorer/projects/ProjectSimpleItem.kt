package com.bentrengrove.projectexplorer.projects

import android.content.res.Resources
import android.net.Uri
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.projectexplorer.util.SimpleItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class ProjectSimpleItem(
    override val id: String,
    override val title: String,
    override val subtitle: String?,
    override val imageUri: Uri?,
    val number: Int
) : SimpleItem {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    }
}

fun ProjectsQuery.Node.toSimpleItem(): ProjectSimpleItem {
    val date = this.updatedAt as? Instant
    val dateString = ProjectSimpleItem.dateFormatter
        .withLocale(Resources.getSystem().configuration.locales.get(0))
        .withZone(ZoneId.systemDefault())
        .format(date)
    return ProjectSimpleItem(id, name, "Updated: $dateString", null, number)
}