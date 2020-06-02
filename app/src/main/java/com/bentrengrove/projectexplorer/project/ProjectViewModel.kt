package com.bentrengrove.projectexplorer.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.projectexplorer.util.Logger
import javax.inject.Inject

sealed class ProjectViewState {
    object Loading: ProjectViewState()
    data class Loaded(val project: ProjectQuery.Project): ProjectViewState()
    object LoadedEmpty: ProjectViewState()
    data class Error(val error: Exception): ProjectViewState()
}

class ProjectViewModel @Inject constructor(private val dataRepository: DataRepository): ViewModel() {
    private val _project: MutableLiveData<ProjectViewState> = MutableLiveData(ProjectViewState.Loading)
    val project: LiveData<ProjectViewState>
        get() = _project

    fun setup(name: String, owner: String, number: Int) {
        dataRepository.apolloClient.query(
            ProjectQuery(owner, name, number, 100)
        ).enqueue(object : ApolloCall.Callback<ProjectQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Logger.e( "Could not load project", e)
                _project.postValue(ProjectViewState.Error(e))
            }

            override fun onResponse(response: Response<ProjectQuery.Data>) {
                Logger.d( "Loaded project $response")
                val project = response.data()?.repository?.project
                if (project != null) {
                    if (project.columns.nodes?.isNotEmpty() == true) {
                        _project.postValue(ProjectViewState.Loaded(project))
                    } else {
                        _project.postValue(ProjectViewState.LoadedEmpty)
                    }
                }
            }
        })
    }
}