package com.bentrengrove.projectexplorer

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.bentrengrove.ProjectQuery
import com.bentrengrove.ProjectsQuery
import com.bentrengrove.RepositoriesQuery
import com.bentrengrove.projectexplorer.project.ProjectViewState
import com.bentrengrove.projectexplorer.projects.ProjectsViewState
import com.bentrengrove.projectexplorer.util.Logger
import okhttp3.OkHttpClient

class DataRepository(private val httpClient: OkHttpClient, val apolloClient: ApolloClient) {
    fun loadRepositories(callback: (Result<Response<RepositoriesQuery.Data>>)->Unit) {
        apolloClient.query(
            RepositoriesQuery(100)
        ).enqueue(createResult(callback))
    }

    suspend fun loadProjects(owner: String, repoName: String): Response<ProjectsQuery.Data> {
        return apolloClient.query(
            ProjectsQuery(owner, repoName, 25)
        ).await()
    }

    fun loadProject(name: String, owner: String, number: Int, callback: (Result<Response<ProjectQuery.Data>>)->Unit) {
        apolloClient.query(
            ProjectQuery(owner, name, number, 100)
        ).enqueue(createResult(callback))
    }

    private fun <T> createResult(callback: (Result<Response<T>>)->Unit): ApolloCall.Callback<T> {
        return object : ApolloCall.Callback<T>() {
            override fun onFailure(e: ApolloException) {
                callback(Result.failure(e))
            }

            override fun onResponse(response: Response<T>) {
                callback(Result.success(response))
            }
        }
    }
}