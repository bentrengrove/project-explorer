package com.bentrengrove.projectexplorer.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.projects.EmptyListView
import com.bentrengrove.projectexplorer.projects.ErrorView
import com.bentrengrove.projectexplorer.projects.ProjectsViewState
import com.bentrengrove.projectexplorer.repositories.LoadingProgress
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import com.bentrengrove.projectexplorer.util.Pager
import com.bentrengrove.projectexplorer.util.PagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class ProjectFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ProjectScreen(onItemClick = this@ProjectFragment::cardClicked)
            }
        }
    }

    private fun cardClicked(card: ProjectQuery.Node1) {
        val content = card.content ?: return
        val url = content.asIssue?.url ?: content.asPullRequest?.url
        if ((url as? String) != null) {
            val uri = Uri.parse(url as? String)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            startActivity(intent)
        }
    }
}

@Composable
fun ProjectScreen(onItemClick: (ProjectQuery.Node1) -> Unit) {
    ProjectTheme {
        val viewModel = viewModel<ProjectViewModel>()
        val state by viewModel.project.observeAsState()

        when (state) {
            ProjectViewState.Loading -> {
                LoadingProgress(Modifier.wrapContentSize())
            }
            is ProjectViewState.Loaded -> {
                val project = (state as ProjectViewState.Loaded).project
                ProjectPager(project = project, onItemClick = onItemClick)
            }
            ProjectViewState.LoadedEmpty -> EmptyListView()
            is ProjectViewState.Error -> ErrorView(error = (state as ProjectsViewState.Error).message ?: "Unknown error")
            null -> { }
        }
    }
}

@Composable
fun ProjectPager(project: ProjectQuery.Project, onItemClick: (ProjectQuery.Node1) -> Unit) {
    val columns = project.columns.nodes?.filterNotNull() ?: listOf()
    val pagerState = remember { PagerState(maxPage = columns.size-1) }

    Pager(state = pagerState, modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        val item = columns[page]
        ProjectColumn(column = item)
    }
}

@Composable
fun ProjectColumn(column: ProjectQuery.Node) {
    val color = remember {
        val random = Random.Default
        Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))
    }
    Box(modifier = Modifier.background(color).padding(4.dp)
        .fillMaxHeight().fillMaxWidth())

}