package com.bentrengrove.projectexplorer.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.util.SimpleItemAdapter
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.projects_fragment.*
import kotlinx.android.synthetic.main.simple_list_item.*
import javax.inject.Inject

@AndroidEntryPoint
class ProjectsFragment : Fragment() {
    @Inject lateinit var viewModelProvider: ViewModelProvider.Factory
    private val viewModel by viewModels<ProjectsViewModel> { viewModelProvider }

    val args: ProjectsFragmentArgs by navArgs()
    private val adapter = SimpleItemAdapter(this::itemSelected)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.projects_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setup(args.ownerName, args.repoName)

        ViewCompat.setTransitionName(repoRow, "shared_element_container")
        lblText1.text = args.repoName
        lblText2.text = args.ownerName

        recyclerView.adapter = adapter
        viewModel.projects.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ProjectsViewState.Loading -> {
                    loadingProgress.isVisible = true
                    errorLayout.isVisible = false
                }
                is ProjectsViewState.Loaded -> {
                    val items = state.projects.nodes?.mapNotNull { it?.toSimpleItem() }
                    adapter.submitList(items)

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = false
                }
                is ProjectsViewState.LoadedEmpty -> {
                    imgError.setImageResource(R.drawable.ic_list_black_24dp)
                    lblError.text = getString(R.string.projects_empty)

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
                is ProjectsViewState.Error -> {
                    imgError.setImageResource(R.drawable.ic_error_outline_black_24dp)
                    lblError.text = state.error.message

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
            }
        })
    }

    private fun itemSelected(index: Int, project: ProjectSimpleItem) {
        val action = ProjectsFragmentDirections.actionProjectsFragmentToProjectFragment(args.ownerName, args.repoName, project.number, project.title)
        findNavController().navigate(action)
    }
}
