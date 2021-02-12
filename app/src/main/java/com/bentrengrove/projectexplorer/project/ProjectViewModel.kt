package com.bentrengrove.projectexplorer.project

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectQuery
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.projectexplorer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProjectViewState {
    object Loading : ProjectViewState()
    data class Loaded(val project: ProjectQuery.Project) : ProjectViewState()
    object LoadedEmpty : ProjectViewState()
    data class Error(val error: Throwable) : ProjectViewState()
}

@HiltViewModel
class ProjectViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle, private val dataRepository: DataRepository) :
    ViewModel() {
    private val _project: MutableLiveData<ProjectViewState> =
        MutableLiveData(ProjectViewState.Loading)
    val project: LiveData<ProjectViewState>
        get() = _project

    init {
        setup()
    }

    fun setup() {
        val owner: String = savedStateHandle.get<String>("ownerName")!!
        val repoName: String = savedStateHandle.get<String>("repoName")!!
        val number: Int = savedStateHandle.get<Int>("number")!!

        viewModelScope.launch {
            val response = try {
                dataRepository.loadProject(repoName, owner, number)
            } catch (e: ApolloException) {
                Logger.e("Could not load project: ${e.message}")
                _project.postValue(ProjectViewState.Error(e))
                return@launch
            }

            val project = response.data?.repository?.project
            if (project != null && project.columns.nodes?.isNotEmpty() == true) {
                _project.postValue(ProjectViewState.Loaded(project))
            } else {
                _project.postValue(ProjectViewState.LoadedEmpty)
            }
        }
    }
}