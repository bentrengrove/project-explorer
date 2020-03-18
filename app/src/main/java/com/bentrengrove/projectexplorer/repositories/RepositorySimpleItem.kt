package com.bentrengrove.projectexplorer.repositories

import android.net.Uri
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.util.SimpleItem

data class RepositorySimpleItem(
    override val id: String,
    override val title: String,
    override val subtitle: String?,
    override val imageUri: Uri?,
    val owner: String
) : SimpleItem

fun RepositoriesQuery.Node.toSimpleItem(): RepositorySimpleItem {
    return RepositorySimpleItem(this.id, this.name, this.owner.login, Uri.parse(this.openGraphImageUrl as? String), this.owner.login)
}