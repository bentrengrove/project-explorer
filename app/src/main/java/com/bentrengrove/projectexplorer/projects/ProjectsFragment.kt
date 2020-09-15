package com.bentrengrove.projectexplorer.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.util.SimpleItemAdapter
import com.google.android.material.transition.MaterialContainerTransform
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment() {
    private val viewModel by viewModels<ProjectsViewModel>()

    val args: ProjectsFragmentArgs by navArgs()
    private val adapter = SimpleItemAdapter(this::itemSelected)

    private lateinit var lblText1: TextView
    private lateinit var lblText2: TextView
    private lateinit var repoRow: ViewGroup
    private lateinit var imgIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var errorLayout: ViewGroup
    private lateinit var imgError: ImageView
    private lateinit var lblError: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.projects_fragment, container, false)

        lblText1 = view.findViewById(R.id.lblText1)
        lblText2 = view.findViewById(R.id.lblText2)
        repoRow = view.findViewById(R.id.repoRow)
        imgIcon = view.findViewById(R.id.imgIcon)
        recyclerView = view.findViewById(R.id.recyclerView)
        loadingProgress = view.findViewById(R.id.loadingProgress)
        errorLayout = view.findViewById(R.id.errorLayout)
        imgError = view.findViewById(R.id.imgError)
        lblError = view.findViewById(R.id.lblError)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setup(args.ownerName, args.repoName)

        lblText1.text = args.repoName
        lblText2.text = args.ownerName
        Picasso.get().load(args.imageUrl).into(imgIcon)

        requireParentFragment()
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
