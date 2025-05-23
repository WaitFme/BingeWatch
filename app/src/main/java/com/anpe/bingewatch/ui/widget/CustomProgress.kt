package com.anpe.bingewatch.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun CustomProgress(
    modifier: Modifier = Modifier,
    currentValue: Int,
    maxValue: Int,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    var canvasWidth by remember {
        mutableFloatStateOf(1f)
    }

    val progressWidth by animateFloatAsState(
        targetValue = if (maxValue == 0 && currentValue == 0) {
            0f
        } else {
            canvasWidth * (currentValue / maxValue.toFloat())
        },
        label = "progress width"
    )

    Canvas(modifier = modifier.size(200.dp, 10.dp)) {
        val canvasHeight = size.height
        canvasWidth = size.width

        /*drawLine(
            color = secondaryColor,
            start = Offset(0f, 0f),
            end = Offset(canvasWidth, 0f),
            strokeWidth = 20f,
            cap = StrokeCap.Round
        )*/

        drawLine(
            color = secondaryColor,
            start = Offset(progressWidth, canvasHeight / 2),
            end = Offset(canvasWidth, canvasHeight / 2),
            strokeWidth = canvasHeight,
            cap = StrokeCap.Round
        )

        drawLine(
            color = primaryColor,
            start = Offset(0f, canvasHeight / 2),
            end = Offset(progressWidth, canvasHeight / 2),
            strokeWidth = canvasHeight,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun MyProgress(
    modifier: Modifier = Modifier,
    progress: Float,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    val coercedProgress = progress.coerceIn(0f, 1f)

    var canvasWidth by remember {
        mutableFloatStateOf(1f)
    }

    val progressWidth by animateFloatAsState(
        targetValue = canvasWidth * coercedProgress,
        label = "progress width"
    )

    Canvas(modifier = modifier.size(200.dp, 10.dp)) {
        val canvasHeight = size.height
        canvasWidth = size.width

        drawRoundRect(
            color = secondaryColor,
            topLeft = Offset(0f, 0f),
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(canvasHeight / 2, canvasHeight / 2)
        )

        drawRoundRect(
            color = primaryColor,
            topLeft = Offset(0f, 0f),
            size = Size(progressWidth, canvasHeight),
            cornerRadius = CornerRadius(canvasHeight / 2, canvasHeight / 2)
        )
    }
}