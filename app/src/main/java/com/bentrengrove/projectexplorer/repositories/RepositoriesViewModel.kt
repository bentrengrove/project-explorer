package com.bentrengrove.projectexplorer.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.Data

sealed class RepositoriesViewState {
    object Loading: RepositoriesViewState()
    data class Loaded(val repositories: RepositoriesQuery.Repositories): RepositoriesViewState()
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
                Log.e("Project", "Could not load", e)
                _repositories.postValue(RepositoriesViewState.Error(e))
            }

            override fun onResponse(response: Response<RepositoriesQuery.Data>) {
                Log.d("Project", "$response ${response.data()?.viewer?.name}")
                val repositories = response.data()?.viewer?.repositories
                if (repositories != null) {
                    if (repositories.nodes?.isNotEmpty() == true) {
                        _repositories.postValue(RepositoriesViewState.Loaded(repositories))
                    } else {
                        _repositories.postValue(RepositoriesViewState.LoadedEmpty)
                    }
                }
            }
        })
    }
}
