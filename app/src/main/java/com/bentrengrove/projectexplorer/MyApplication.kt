package com.bentrengrove.projectexplorer

import android.app.Application
import com.apollographql.apollo.ApolloClient
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {
    @Inject lateinit var apolloClient: ApolloClient // Exposed for testing

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }
}