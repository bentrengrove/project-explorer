package com.bentrengrove.projectexplorer.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
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
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.repositories.LoadingProgress
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import com.bentrengrove.type.ProjectState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment() {
    private val viewModel: ProjectsViewModel by viewModels()
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

    private fun itemSelected(project: ProjectSimpleItem) {
        val action = ProjectsFragmentDirections.actionProjectsFragmentToProjectFragment(args.ownerName, args.repoName, project.number, project.title)
        findNavController().navigate(action)
    }
}

@Composable
fun ProjectsScreen(onItemClick: (ProjectSimpleItem)->Unit) {
    ProjectTheme {
        val viewModel = viewModel<ProjectsViewModel>()
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
            is ProjectsViewState.Error -> ErrorView(error = (state as ProjectsViewState.Error).message ?: "Unknown error")
            null -> { }
        }
    }
}

@Composable
fun ProjectItem(item: ProjectSimpleItem, onClick: (ProjectSimpleItem) -> Unit) {
    val body = if (item.body.isNullOrEmpty()) "No description" else item.body

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { onClick(item) })
        .padding(8.dp)
    ) {
        Text(text = item.title, style = MaterialTheme.typography.h6)
        Text(text = item.subtitle ?: "", style = MaterialTheme.typography.caption)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = body, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun ProjectsList(projects: List<ProjectSimpleItem>, onClick: (ProjectSimpleItem) -> Unit) {
    LazyColumn {
        projects.forEach { project ->
            item {
                ProjectItem(item = project, onClick = onClick)
                Divider()
            }
        }
    }
}

@Preview
@Composable
fun ProjectItemPreview() {
    ProjectTheme {
        val item = ProjectSimpleItem("123", "Release", "Subtitle", null, "No description", 1, ProjectState.OPEN, null)
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