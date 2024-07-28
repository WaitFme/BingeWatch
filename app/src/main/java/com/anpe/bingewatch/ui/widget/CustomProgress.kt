package com.anpe.bingewatch.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun CustomProgress(
    modifier: Modifier = Modifier,
    currentValue: Int,
    maxValue: Int,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Gray
) {

    val ww = remember {
        mutableStateOf(1f)
    }

    val w by animateFloatAsState(
        targetValue = if (maxValue == 0 && currentValue == 0)
            0f
        else
            ww.value * (currentValue / maxValue.toFloat())
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 画布的高
        val canvasHeight = size.height
        // 画布的宽
        val canvasWidth = size.width
        ww.value = canvasWidth

        drawLine(
            color = secondaryColor,
            start = Offset(0f, 0f),
            end = Offset(canvasWidth, 0f),
            strokeWidth = 20f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = primaryColor,
            start = Offset(0f, 0f),
            end = Offset(
                w,
                0f
            ),
            strokeWidth = 20f,
            cap = StrokeCap.Round
        )
    }
}