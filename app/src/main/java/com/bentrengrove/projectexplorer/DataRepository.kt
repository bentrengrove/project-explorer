package com.bentrengrove.projectexplorer

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class DataRepository(private val httpClient: OkHttpClient, val apolloClient: ApolloClient) {

}