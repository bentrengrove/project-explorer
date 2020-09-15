package com.bentrengrove.projectexplorer.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import com.bentrengrove.projectexplorer.util.SimpleItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

@AndroidEntryPoint
class RepositoriesFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                RepositoriesScreen(onItemClick = {
                    itemSelected(it.toSimpleItem())
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
fun RepositoriesScreen(onItemClick: (RepositoriesQuery.Node) -> Unit) {
    ProjectTheme {
        val viewModel: RepositoriesViewModel = viewModel()
        val state by viewModel.repositories.observeAsState()

        when(state) {
            is RepositoriesViewState.Loading -> {
                LoadingProgress()
            }
            is RepositoriesViewState.Loaded -> {
                RepositoriesList(repositories = (state as RepositoriesViewState.Loaded).repositories, onItemClick)
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
    Text("Loading...")
}

@Composable
fun RepositoriesList(repositories: List<RepositoriesQuery.Node>, onItemClick: (RepositoriesQuery.Node) -> Unit) {
    LazyColumnFor(items = repositories) { item ->
        RepositoryItem(item = item, onItemClick)
    }
}

@Composable
fun RepositoryItem(item: RepositoriesQuery.Node, onClick: (RepositoriesQuery.Node) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { onClick(item) })) {
        Surface(shape = CircleShape,
            modifier = Modifier.padding(8.dp).size(56.dp),
            color = MaterialTheme.colors.primary) {}

        Column(modifier = Modifier.gravity(Alignment.CenterVertically)) {
            Text(text = item.name)
            Text(text = item.owner.login)
        }
    }
}

@Composable
fun EmptyListView() {
    Text("There were no repositories found")
}

@Composable
fun ErrorView(error: String) {
    Text(text = error)
}