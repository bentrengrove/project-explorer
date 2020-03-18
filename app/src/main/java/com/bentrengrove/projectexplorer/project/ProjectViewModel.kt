package com.bentrengrove.projectexplorer.project

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.Data

sealed class ProjectViewState {
    object Loading: ProjectViewState()
    data class Loaded(val project: ProjectQuery.Project): ProjectViewState()
    object LoadedEmpty: ProjectViewState()
    data class Error(val error: Exception): ProjectViewState()
}

class ProjectViewModel: ViewModel() {
    private val _project: MutableLiveData<ProjectViewState> = MutableLiveData(ProjectViewState.Loading)
    val project: LiveData<ProjectViewState>
        get() = _project

    fun setup(name: String, owner: String, number: Int) {
        Data.apolloClient.query(
            ProjectQuery(owner, name, number, 100)
        ).enqueue(object : ApolloCall.Callback<ProjectQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                Log.e("Project", "Could not load project", e)
                _project.postValue(ProjectViewState.Error(e))
            }

            override fun onResponse(response: Response<ProjectQuery.Data>) {
                Log.d("Project", "Loaded project $response")
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