package com.bentrengrove.projectexplorer.login

import com.bentrengrove.projectexplorer.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager {
    private var _accessToken = MutableStateFlow(if (BuildConfig.GITHUB_TOKEN.isNotEmpty()) BuildConfig.GITHUB_TOKEN else null)
    var accessToken: StateFlow<String?> = _accessToken

    fun login(token: String) {
        _accessToken.value = token
    }

    fun logout() {
        _accessToken.value = null
    }
}