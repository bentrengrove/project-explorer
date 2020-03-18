package com.bentrengrove.projectexplorer.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.projectexplorer.Data

sealed class ProjectsViewState {
    object Loading: ProjectsViewState()
    data class Loaded(val projects: ProjectsQuery.Projects): ProjectsViewState()
    object LoadedEmpty: ProjectsViewState()
    data class Error(val error: Exception): ProjectsViewState()
}

class ProjectsViewModel: ViewModel() {
    private val _projects: MutableLiveData<ProjectsViewState> = MutableLiveData(ProjectsViewState.Loading)
    val projects: LiveData<ProjectsViewState>
        get() = _projects

    fun setup(owner: String, repoName: String) {
        Data.apolloClient.query(
            ProjectsQuery(owner, repoName)
        ).enqueue(object : ApolloCall.Callback<ProjectsQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Log.e("Project", "Could not load projects", e)
                _projects.postValue(ProjectsViewState.Error(e))
            }

            override fun onResponse(response: Response<ProjectsQuery.Data>) {
                Log.d("Project", "$response")
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