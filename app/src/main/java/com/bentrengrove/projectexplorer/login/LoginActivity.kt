package com.bentrengrove.projectexplorer.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.bentrengrove.projectexplorer.repositories.LoadingProgress
import com.bentrengrove.projectexplorer.theme.ProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startLogin()

        setContent {
            ProjectTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Login()
                }
            }
        }
    }
}

@Composable
private fun Login() {
    val state by viewModel<LoginViewModel>().state.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text(text = "Sign in to GitHub", style = MaterialTheme.typography.h4)
        when (state) {
            is LoginViewState.Loading -> LoadingState()
            is LoginViewState.CodeReceived -> CodeLoaded(codeResponse = state as LoginViewState.CodeReceived)
            is LoginViewState.Error -> Text((state as LoginViewState.Error).exception.message ?: "Unknown error")
            LoginViewState.Success -> {
                (LocalContext.current as? Activity)?.finish()
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    LoadingProgress(modifier)
}

private const val TAG_URL = "url"
@Composable
private fun CodeLoaded(codeResponse: LoginViewState.CodeReceived, modifier: Modifier = Modifier) {
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(codeResponse.url) }
    val context = LocalContext.current
    val urlString = buildAnnotatedString {
        append("To sign in to GitHub browse to ")
        val underline = SpanStyle(MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline)
        pushStyle(underline)
        pushStringAnnotation(TAG_URL, codeResponse.url)
        append(codeResponse.url)
        pop()
        pop()
        append(" and enter the code below.")
    }

    Column(modifier) {
        Text(text = urlString, style = MaterialTheme.typography.body1, modifier = Modifier.clickable { context.startActivity(intent) })
        Text(text = codeResponse.code, style = MaterialTheme.typography.h3)
        LoadingProgress()
    }
}