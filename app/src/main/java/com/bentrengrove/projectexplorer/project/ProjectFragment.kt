package com.bentrengrove.projectexplorer.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.R
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.project_fragment.*
import kotlinx.android.synthetic.main.project_fragment.errorLayout
import kotlinx.android.synthetic.main.project_fragment.loadingProgress
import kotlinx.android.synthetic.main.simple_list_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class ProjectFragment: Fragment() {
    private val viewModel by viewModels<ProjectViewModel>()
    private val args: ProjectFragmentArgs by navArgs()
    private var tabMediator: TabLayoutMediator? = null
    private lateinit var viewPagerAdapter: ProjectPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.project_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPagerAdapter = ProjectPagerAdapter(Markwon.create(viewPager.context), this::cardClicked)
        viewPager.adapter = viewPagerAdapter

        viewModel.setup(args.repoName, args.ownerName, args.number)

        viewModel.project.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ProjectViewState.Loading -> {
                    loadingProgress.isVisible = true
                    errorLayout.isVisible = false
                }
                is ProjectViewState.Loaded -> {
                    setupForProject(it.project)

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = false
                }
                is ProjectViewState.LoadedEmpty -> {
                    imgError.setImageResource(R.drawable.ic_list_black_24dp)
                    lblError.text = getString(R.string.project_empty)
                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
                is ProjectViewState.Error -> {
                    imgError.setImageResource(R.drawable.ic_error_outline_black_24dp)
                    lblError.text = it.error.message

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
            }
        })
    }

    private fun setupForProject(project: ProjectQuery.Project) {
        tabMediator?.detach()
        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = project.columns.nodes?.get(position)?.name
        }.apply { attach() }

        viewPagerAdapter.submitList(project.columns.nodes)
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