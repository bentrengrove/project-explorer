package com.bentrengrove.projectexplorer.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class ProjectFragment: Fragment() {
    private val viewModel by viewModels<ProjectViewModel>()
    private val args: ProjectFragmentArgs by navArgs()
    private var tabMediator: TabLayoutMediator? = null
    private lateinit var viewPagerAdapter: ProjectPagerAdapter

    private lateinit var viewPager: ViewPager2
    private lateinit var loadingProgress: ProgressBar
    private lateinit var errorLayout: ViewGroup
    private lateinit var imgError: ImageView
    private lateinit var lblError: TextView
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.project_fragment, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        loadingProgress = view.findViewById(R.id.loadingProgress)
        errorLayout = view.findViewById(R.id.errorLayout)
        imgError = view.findViewById(R.id.imgError)
        lblError = view.findViewById(R.id.lblError)
        tabLayout = view.findViewById(R.id.tabLayout)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPagerAdapter = ProjectPagerAdapter(Markwon.create(viewPager.context), this::cardClicked)
        viewPager.adapter = viewPagerAdapter

        //viewModel.setup(args.repoName, args.ownerName, args.number)

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