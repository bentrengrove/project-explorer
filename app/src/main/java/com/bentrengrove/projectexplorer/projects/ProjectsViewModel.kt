package com.bentrengrove.projectexplorer.projects

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.projectexplorer.util.Logger
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProjectsViewState {
    object Loading: ProjectsViewState()
    data class Loaded(val projects: ProjectsQuery.Projects): ProjectsViewState()
    object LoadedEmpty: ProjectsViewState()
    data class Error(val error: Throwable): ProjectsViewState()
}

class ProjectsViewModel @ViewModelInject constructor(private val dataRepository: DataRepository): ViewModel() {
    private val _projects: MutableLiveData<ProjectsViewState> = MutableLiveData(ProjectsViewState.Loading)
    val projects: LiveData<ProjectsViewState>
        get() = _projects

    fun setup(owner: String, repoName: String) {
        viewModelScope.launch {
            val response = try {
                dataRepository.loadProjects(owner, repoName)
            } catch (e: ApolloException) {
                _projects.postValue(ProjectsViewState.Error(e))
                Logger.e( "Could not load projects", Exception(e))
                return@launch
            }

            val projects = response.data?.repository?.projects
            if (projects != null && projects.nodes?.isNotEmpty() == true) {
                _projects.postValue(ProjectsViewState.Loaded(projects))
            } else {
                _projects.postValue(ProjectsViewState.LoadedEmpty)
            }
        }
    }
}