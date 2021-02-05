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
    //TODO: These apis need to integrate paging, currently just using dummy numbers
    suspend fun loadRepositories(): Response<RepositoriesQuery.Data> {
        return apolloClient.query(
            RepositoriesQuery(100)
        ).await()
    }

    suspend fun loadProjects(owner: String, repoName: String): Response<ProjectsQuery.Data> {
        return apolloClient.query(
            ProjectsQuery(owner, repoName, 25)
        ).await()
    }

    suspend fun loadProject(name: String, owner: String, number: Int): Response<ProjectQuery.Data> {
        return apolloClient.query(
            ProjectQuery(owner, name, number, 100)
        ).await()
    }
}