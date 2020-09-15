package com.bentrengrove.projectexplorer.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ProjectTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}