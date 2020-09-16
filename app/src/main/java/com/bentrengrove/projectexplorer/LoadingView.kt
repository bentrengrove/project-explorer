package com.bentrengrove.projectexplorer

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.min
import kotlin.math.sin

private const val NUM_DOTS = 16

/**
 * Creates a composable ring-of-dots animation.
 *
 * Heavily inspired and referenced from
 * https://twitter.com/alexjlockwood/status/1269417448651886592
 */
@Composable
fun RingOfDots(modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.primary) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 1000, easing = LinearEasing),
            ),
        )
    }

    val t = animatedProgress.value
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val ringRadius = min(width, height) * 0.35f
        val waveRadius = min(width, height) * 0.10f
        val dotRadius = waveRadius / 3f

        for (i in 0..NUM_DOTS) {
            drawDot(i, t, ringRadius, waveRadius, dotRadius, color)
        }
    }
}

private fun DrawScope.drawDot(
    index: Int,
    t: Float,
    ringRadius: Float,
    waveRadius: Float,
    dotRadius: Float,
    color: Color
) {
    val dotAngle = (index / NUM_DOTS.toDouble()) * (2 * Math.PI)
    val waveAngle = (dotAngle + (t * 2 * Math.PI))

    val waveMagnitude = sin(waveAngle) * waveRadius
    val alpha = ((sin(waveAngle).toFloat() + 1f) / 2f).coerceAtLeast(0.05f)

    withTransform({
        rotate(Math.toDegrees(dotAngle).toFloat())
        translate((ringRadius + waveMagnitude).toFloat(), 0f)
    }, {
        drawCircle(color.copy(alpha = alpha), radius = dotRadius)
    })
}