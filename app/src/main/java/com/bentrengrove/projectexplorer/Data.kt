package com.bentrengrove.projectexplorer

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.fetcher.ResponseFetcher
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.apollographql.apollo.internal.ApolloLogger
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executor


object Data {
    // This structure allows us to mock out different dependencies if needed for testing
    private lateinit var httpClient: OkHttpClient

    private lateinit var _apolloClient: ApolloClient
    val apolloClient: ApolloClient
        get() = _apolloClient

    fun setupApp() {
        httpClient = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val builder: Request.Builder =
                    original.newBuilder().method(original.method(), original.body())
                builder.header("Authorization", "bearer ${BuildConfig.GITHUB_TOKEN}")
                chain.proceed(builder.build())
            }
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        _apolloClient = ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(httpClient)
            .build()
    }
}