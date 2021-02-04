package com.bentrengrove.projectexplorer.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.bentrengrove.projectexplorer.BuildConfig
import com.bentrengrove.projectexplorer.DataRepository
import com.bentrengrove.type.CustomType
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val builder: Request.Builder =
                    original.newBuilder().method(original.method(), original.body())
                builder.header("Authorization", "bearer ${BuildConfig.GITHUB_TOKEN}")
                chain.proceed(builder.build())
            }
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideApollo(httpClient: OkHttpClient): ApolloClient {
        val dateAdapter = object : CustomTypeAdapter<Instant> {
            override fun encode(value: Instant): CustomTypeValue<*> {
                return CustomTypeValue.GraphQLString(DateTimeFormatter.ISO_INSTANT.format(value))
            }

            override fun decode(value: CustomTypeValue<*>): Instant {
                return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value.value.toString()))
            }
        }

        return ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(httpClient)
            .addCustomTypeAdapter(CustomType.DATETIME, dateAdapter)
            .build()
    }

    @Provides
    @Singleton
    fun provideDataRepository(okHttpClient: OkHttpClient, apolloClient: ApolloClient): DataRepository {
        return DataRepository(okHttpClient, apolloClient)
    }
}