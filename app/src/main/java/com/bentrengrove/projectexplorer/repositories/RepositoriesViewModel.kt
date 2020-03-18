package com.bentrengrove.projectexplorer.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.Data
import com.bentrengrove.projectexplorer.util.Logger

sealed class RepositoriesViewState {
    object Loading: RepositoriesViewState()
    data class Loaded(val repositories: List<RepositoriesQuery.Node>): RepositoriesViewState()
    object LoadedEmpty: RepositoriesViewState()
    data class Error(val error: Exception): RepositoriesViewState()
}

class RepositoriesViewModel : ViewModel() {
    private val _repositories: MutableLiveData<RepositoriesViewState> = MutableLiveData(RepositoriesViewState.Loading)
    val repositories: LiveData<RepositoriesViewState>
        get() = _repositories

    init {
        Data.apolloClient.query(
            RepositoriesQuery(100)
        ).enqueue(object : ApolloCall.Callback<RepositoriesQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Logger.e( "Could not load", e)
                _repositories.postValue(RepositoriesViewState.Error(e))
            }

            override fun onResponse(response: Response<RepositoriesQuery.Data>) {
                Logger.d( "$response ${response.data()?.viewer?.name}")
                val nodes = response.data()?.viewer?.repositories?.nodes
                val repositories = nodes?.filter { it?.projects?.totalCount != null && it.projects.totalCount > 0 }?.filterNotNull()

                if (repositories != null) {
                    if (repositories.isNotEmpty()) {
                        _repositories.postValue(RepositoriesViewState.Loaded(repositories))
                    } else {
                        _repositories.postValue(RepositoriesViewState.LoadedEmpty)
                    }
                }
            }
        })
    }
}
