package com.bentrengrove.projectexplorer.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.util.SimpleItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class RepositoriesFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        fun newInstance() = RepositoriesFragment()
    }

    private val viewModel: RepositoriesViewModel by viewModels()
    private val adapter = SimpleItemAdapter<RepositorySimpleItem>(this::itemSelected)

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var errorLayout: ViewGroup
    private lateinit var imgError: ImageView
    private lateinit var lblError: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.simple_list_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        loadingProgress = view.findViewById(R.id.loadingProgress)
        errorLayout = view.findViewById(R.id.errorLayout)
        imgError = view.findViewById(R.id.imgError)
        lblError = view.findViewById(R.id.lblError)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter
        viewModel.repositories.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is RepositoriesViewState.Loading -> {
                    loadingProgress.isVisible = true
                    errorLayout.isVisible = false
                }
                is RepositoriesViewState.Loaded -> {
                    val items = state.repositories.map { it.toSimpleItem() }
                    adapter.submitList(items)

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = false
                }
                is RepositoriesViewState.LoadedEmpty -> {
                    imgError.setImageResource(R.drawable.ic_list_black_24dp)
                    lblError.text = getString(R.string.repositories_empty)

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
                is RepositoriesViewState.Error -> {
                    imgError.setImageResource(R.drawable.ic_error_outline_black_24dp)
                    lblError.text = state.error.message

                    loadingProgress.isVisible = false
                    errorLayout.isVisible = true
                }
            }
        })
    }

    private fun itemSelected(position: Int, repositorySimpleItem: RepositorySimpleItem) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? SimpleItemAdapter.ViewHolder ?: return
        ViewCompat.setTransitionName(viewHolder.containerView, "shared_element_container")
        val extras = FragmentNavigatorExtras(viewHolder.containerView to "shared_element_container")
        val action = RepositoriesFragmentDirections.actionRepositoriesFragmentToProjectsFragment(repositorySimpleItem.owner, repositorySimpleItem.title, repositorySimpleItem.imageUri?.toString())
        findNavController().navigate(action, extras)
    }
}
