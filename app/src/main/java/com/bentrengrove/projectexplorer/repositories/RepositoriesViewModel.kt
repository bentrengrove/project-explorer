package com.bentrengrove.projectexplorer.repositories

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.projectexplorer.login.SessionManager
import com.bentrengrove.projectexplorer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RepositoriesViewState {
    object Loading: RepositoriesViewState()
    data class Loaded(val repositories: List<RepositoriesQuery.Node>): RepositoriesViewState()
    object LoadedEmpty: RepositoriesViewState()
    data class Error(val error: Throwable): RepositoriesViewState()
}

@HiltViewModel
class RepositoriesViewModel @Inject constructor(private val dataRepository: DataRepository, val sessionManager: SessionManager) : ViewModel() {
    private val _repositories: MutableLiveData<RepositoriesViewState> = MutableLiveData(RepositoriesViewState.Loading)
    val repositories: LiveData<RepositoriesViewState>
        get() = _repositories

    init {
        viewModelScope.launch {
            sessionManager.accessToken.collect { token ->
                if (token.isNullOrEmpty()) return@collect

                val response = try {
                    dataRepository.loadRepositories()
                } catch (e: ApolloException) {
                    _repositories.postValue(RepositoriesViewState.Error(e))
                    return@collect
                }

                val nodes = response.data?.viewer?.repositories?.nodes
                val repositories = nodes?.filter { it?.projects?.totalCount != null && it.projects.totalCount > 0 }?.filterNotNull()

                if (repositories != null && repositories.isNotEmpty()) {
                    _repositories.postValue(RepositoriesViewState.Loaded(repositories))
                } else {
                    _repositories.postValue(RepositoriesViewState.LoadedEmpty)
                }
            }
        }
    }
}
