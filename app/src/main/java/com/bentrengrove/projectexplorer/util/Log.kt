package com.bentrengrove.projectexplorer.util

import android.util.Log
import java.lang.Exception

const val LOG_TAG = "ProjectExplorer"

object Logger {
    fun d(message: String) {
        Log.d(LOG_TAG, message)
    }

    fun i(message: String) {
        Log.i(LOG_TAG, message)
    }

    fun e(message: String) {
        Log.e(LOG_TAG, message)
    }

    fun e(message: String, e: Exception) {
        Log.e(LOG_TAG, message, e)
    }
}
