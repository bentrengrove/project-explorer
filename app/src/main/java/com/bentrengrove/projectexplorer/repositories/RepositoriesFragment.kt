package com.bentrengrove.projectexplorer.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bentrengrove.projectexplorer.R
import com.bentrengrove.projectexplorer.util.SimpleItemAdapter
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.simple_list_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class RepositoriesFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        fun newInstance() = RepositoriesFragment()
    }

    private lateinit var viewModel: RepositoriesViewModel
    private val adapter = SimpleItemAdapter<RepositorySimpleItem>(this::itemSelected)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.simple_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RepositoriesViewModel::class.java)

        recyclerView.adapter = adapter
        viewModel.repositories.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is RepositoriesViewState.Loading -> {
                    loadingProgress.isVisible = true
                    errorLayout.isVisible = false
                }
                is RepositoriesViewState.Loaded -> {
                    val items = state.repositories.nodes?.mapNotNull { it?.toSimpleItem() }
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

    private fun itemSelected(repositorySimpleItem: RepositorySimpleItem) {
        val action = RepositoriesFragmentDirections.actionRepositoriesFragmentToProjectsFragment(repositorySimpleItem.owner, repositorySimpleItem.title)
        findNavController().navigate(action)
    }
}
