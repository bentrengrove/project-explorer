package com.bentrengrove.projectexplorer

import android.app.Application
import com.facebook.stetho.Stetho

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Data.setupApp()
        Stetho.initializeWithDefaults(this)
    }
}