package com.bentrengrove.projectexplorer

import com.apollographql.apollo.ApolloClient
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

object Data {
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val builder: Request.Builder =
                original.newBuilder().method(original.method(), original.body())
            builder.header("Authorization", "bearer ${BuildConfig.GITHUB_TOKEN}")
            chain.proceed(builder.build())
        }
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl("https://api.github.com/graphql")
        .okHttpClient(httpClient)
        .build()
}