package com.bentrengrove.projectexplorer.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.bentrengrove.projectexplorer.R
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
                Surface(
                    color = MaterialTheme.colors.primary.copy(0.1f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Login()
                }
            }
        }
    }
}

@Composable
private fun Login() {
    val state by viewModel<LoginViewModel>().state.collectAsState()

    Column {
        TopAppBar(title = { Text("Sign in to GitHub") })
        Card(Modifier.padding(16.dp).fillMaxSize()) {
            Column(Modifier.padding(8.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    imageVector = vectorResource(id = R.drawable.ic_login_24),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp).height(128.dp).fillMaxWidth(),
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                )
                when (state) {
                    is LoginViewState.Loading -> LoadingState()
                    is LoginViewState.CodeReceived -> CodeLoaded(codeResponse = state as LoginViewState.CodeReceived)
                    is LoginViewState.Error -> Text(
                        (state as LoginViewState.Error).exception.message ?: "Unknown error"
                    )
                    LoginViewState.Success -> {
                        (LocalContext.current as? Activity)?.finish()
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    LoadingProgress(modifier.wrapContentSize())
}

private const val TAG_URL = "url"

@Composable
private fun CodeLoaded(codeResponse: LoginViewState.CodeReceived) {
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(codeResponse.url) }
    val context = LocalContext.current
    val urlString = buildAnnotatedString {
        append("Before you can use Project Explorer you must authenticate with GitHub.\n\nTo sign in to GitHub browse to ")
        val underline =
            SpanStyle(MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline)
        pushStyle(underline)
        pushStringAnnotation(TAG_URL, codeResponse.url)
        append(codeResponse.url)
        pop()
        pop()
        append(" and enter the code below.")
    }

    Column {
        Text(
            text = urlString,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.clickable { context.startActivity(intent) },
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            text = codeResponse.code,
            style = MaterialTheme.typography.h2,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun CodeLoadedPreview() {
    ProjectTheme {
        CodeLoaded(
            codeResponse = LoginViewState.CodeReceived(
                "ABCD-1234",
                "http://github.com/device/login"
            )
        )
    }
}