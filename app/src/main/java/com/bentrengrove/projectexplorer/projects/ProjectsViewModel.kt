package com.bentrengrove.projectexplorer.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.projectexplorer.util.Logger
import javax.inject.Inject

sealed class ProjectsViewState {
    object Loading: ProjectsViewState()
    data class Loaded(val projects: ProjectsQuery.Projects): ProjectsViewState()
    object LoadedEmpty: ProjectsViewState()
    data class Error(val error: Exception): ProjectsViewState()
}

class ProjectsViewModel @Inject constructor(private val dataRepository: DataRepository): ViewModel() {
    private val _projects: MutableLiveData<ProjectsViewState> = MutableLiveData(ProjectsViewState.Loading)
    val projects: LiveData<ProjectsViewState>
        get() = _projects

    fun setup(owner: String, repoName: String) {
        dataRepository.apolloClient.query(
            ProjectsQuery(owner, repoName, 25)
        ).enqueue(object : ApolloCall.Callback<ProjectsQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Logger.e( "Could not load projects", e)
                _projects.postValue(ProjectsViewState.Error(e))
            }

            override fun onResponse(response: Response<ProjectsQuery.Data>) {
                Logger.d( "$response")
                val projects = response.data()?.repository?.projects
                if (projects != null) {
                    if (projects.nodes?.isNotEmpty() == true) {
                        _projects.postValue(ProjectsViewState.Loaded(projects))
                    } else {
                        _projects.postValue(ProjectsViewState.LoadedEmpty)
                    }
                }
            }
        })
    }
}