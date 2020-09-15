package com.bentrengrove.projectexplorer.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.ui.tooling.preview.Preview
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class RepositoriesFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                RepositoriesScreen(onItemClick = {
                    itemSelected(it)
                })
            }
        }
    }

    private fun itemSelected(repositorySimpleItem: RepositorySimpleItem) {
        val action = RepositoriesFragmentDirections.actionRepositoriesFragmentToProjectsFragment(repositorySimpleItem.owner, repositorySimpleItem.title, repositorySimpleItem.imageUri?.toString())
        findNavController().navigate(action)
    }
}

@Composable
fun RepositoriesScreen(onItemClick: (RepositorySimpleItem) -> Unit) {
    ProjectTheme {
        val viewModel: RepositoriesViewModel = viewModel()
        val state by viewModel.repositories.observeAsState()

        when(state) {
            is RepositoriesViewState.Loading -> {
                LoadingProgress()
            }
            is RepositoriesViewState.Loaded -> {
                RepositoriesList(repositories = (state as RepositoriesViewState.Loaded).repositories.map { it.toSimpleItem() }, onItemClick)
            }
            is RepositoriesViewState.LoadedEmpty -> {
                EmptyListView()
            }
            is RepositoriesViewState.Error -> {
                ErrorView(
                    error = (state as RepositoriesViewState.Error).error.message ?: "Unknown error"
                )
            }
        }
    }
}

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(modifier = Modifier.size(48.dp).gravity(Alignment.CenterVertically))
}

@Composable
fun RepositoriesList(repositories: List<RepositorySimpleItem>, onItemClick: (RepositorySimpleItem) -> Unit) {
    LazyColumnFor(items = repositories) { item ->
        RepositoryItem(item = item, onItemClick)
    }
}

@Composable
fun RepositoryItem(item: RepositorySimpleItem, onClick: (RepositorySimpleItem) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { onClick(item) })) {
        if (item.imageUri != null) {
            CoilImage(data = item.imageUri.toString(), modifier = Modifier.padding(8.dp).size(56.dp).clip(CircleShape))
        } else {
            Surface(shape = CircleShape,
                modifier = Modifier.padding(8.dp).size(56.dp),
                color = MaterialTheme.colors.primary) {}
        }

        Column(modifier = Modifier.gravity(Alignment.CenterVertically)) {
            Text(text = item.title, style = MaterialTheme.typography.body1)
            Text(text = item.owner, style = MaterialTheme.typography.caption)
        }
    }
}

@Preview
@Composable
fun RepositoryItemPreview() {
    val item = RepositorySimpleItem("1", "Project", "Jo Bloggs", null, "Jo Bloggs")
    RepositoryItem(item = item, onClick = {})
}

@Composable
fun EmptyListView() {
    Text("There were no repositories found")
}

@Composable
fun ErrorView(error: String) {
    Text(text = error)
}