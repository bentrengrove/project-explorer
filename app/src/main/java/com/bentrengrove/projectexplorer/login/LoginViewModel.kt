package com.bentrengrove.projectexplorer.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bentrengrove.projectexplorer.BuildConfig
import com.bentrengrove.projectexplorer.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

sealed class LoginViewState {
    object Loading: LoginViewState()
    data class CodeReceived(val code: String, val url: String): LoginViewState()
    object Success: LoginViewState()
    data class Error(val exception: Exception): LoginViewState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(githubRetrofit: Retrofit, val sessionManager: SessionManager): ViewModel() {
    private val _state = MutableStateFlow<LoginViewState>(LoginViewState.Loading)
    val state: StateFlow<LoginViewState> = _state

    private val service = githubRetrofit.create(LoginAPI::class.java)

    private var codeResponse: LoginDeviceCodeResponse? = null

    fun startLogin() {
        viewModelScope.launch {
            try {
                val request = LoginDeviceCodeRequest(BuildConfig.GITHUB_CLIENT_ID, "repo, user")
                val response = service.requestCode(request)
                _state.value = LoginViewState.CodeReceived(response.userCode, response.verificationUri)
                codeResponse = response

                startPolling()
            } catch (e: Exception) {
                Logger.e("$e")
                _state.value = LoginViewState.Error(e)
            }
        }
    }

    private fun startPolling() {
        val codeResponse = codeResponse ?: return

        viewModelScope.launch {
            val pollRequest = LoginDevicePollRequest(BuildConfig.GITHUB_CLIENT_ID, codeResponse.deviceCode)
            var total = codeResponse.expiresIn // TODO: This is very naive as it doesn't take network call time in to account
            var accessCode: String? = null

            while (total > 0 && accessCode == null) {
                delay(codeResponse.interval * 1000)
                total -= codeResponse.interval

                try {
                    val response = service.pollCode(pollRequest)
                    accessCode = response.accessToken
                }
                catch (e: Exception) {
                    Logger.e("Error polling: $e")
                }
            }

            if (accessCode != null) {
                _state.value = LoginViewState.Success
                sessionManager.login(accessCode)
            } else {
                _state.value = LoginViewState.Error(TimeoutException())
            }
        }
    }
}