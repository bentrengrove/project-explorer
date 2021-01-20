package com.bentrengrove.projectexplorer.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bentrengrove.projectexplorer.repositories.LoadingProgress
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment() {
    private val viewModel by viewModels<ProjectsViewModel>()

    val args: ProjectsFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProjectsScreen(onItemClick = this@ProjectsFragment::itemSelected)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setup(args.ownerName, args.repoName)
    }

    private fun itemSelected(project: ProjectSimpleItem) {
        val action = ProjectsFragmentDirections.actionProjectsFragmentToProjectFragment(args.ownerName, args.repoName, project.number, project.title)
        findNavController().navigate(action)
    }
}

@Composable
fun ProjectsScreen(onItemClick: (ProjectSimpleItem)->Unit) {
    ProjectTheme {
        val viewModel: ProjectsViewModel = viewModel()
        val state by viewModel.projects.observeAsState()

        when (state) {
            ProjectsViewState.Loading -> {
                LoadingProgress(Modifier.wrapContentSize())
            }
            is ProjectsViewState.Loaded -> {
                val items = (state as ProjectsViewState.Loaded).projects.nodes?.mapNotNull { it?.toSimpleItem() } ?: listOf()
                ProjectsList(items, onItemClick)
            }
            ProjectsViewState.LoadedEmpty -> EmptyListView()
            is ProjectsViewState.Error -> ErrorView(error = (state as ProjectsViewState.Error).error.message ?: "Unknown error")
            null -> { }
        }
    }
}

@Composable
fun ProjectItem(item: ProjectSimpleItem, onClick: (ProjectSimpleItem) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { onClick(item) })
        .padding(8.dp)
    ) {
        Text(text = item.title, style = MaterialTheme.typography.body1)
        Text(text = item.subtitle ?: "", style = MaterialTheme.typography.caption)
    }
}

@Composable
fun ProjectsList(items: List<ProjectSimpleItem>, onClick: (ProjectSimpleItem) -> Unit) {
    LazyColumn() {
        items(items) { item ->
            ProjectItem(item = item, onClick = onClick)
        }
    }
}

@Preview
@Composable
fun ProjectItemPreview() {
    ProjectTheme {
        val item = ProjectSimpleItem("123", "Release", "Subtitle", null, 1)
        ProjectItem(item = item, onClick = {  })
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