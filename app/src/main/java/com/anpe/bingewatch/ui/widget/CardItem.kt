package com.anpe.bingewatch.ui.widget

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anpe.bingewatch.R
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.utils.Tools.Companion.change
import com.anpe.bingewatch.utils.Tools.Companion.getTime
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun WatchItem(
    modifier: Modifier = Modifier,
    entity: WatchEntity,
    onEpiIncrease: () -> Unit = {},
    onEpiDecrease: () -> Unit = {},
    onDateChange: () -> Unit = {},
    onDialog: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var offsetX by remember {
        mutableFloatStateOf(0f)
    }
    var xDisplay by remember {
        mutableFloatStateOf(0f)
    }
    var xDrag by remember {
        mutableFloatStateOf(0f)
    }
    val offsetYAnimate by animateFloatAsState(targetValue = offsetX, label = "")
    var width by remember { mutableFloatStateOf(0f) }
    var widthLimit by remember { mutableFloatStateOf(0f) }

    val trigger = width * 0.25

    val showIcon = if (offsetX > trigger) {
        1
    } else if (offsetX < -trigger) {
        2
    } else {
        0
    }

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    vibrator.vibrate(50)
                                }
                            }
                            onDialog()
                        }
                    )
                }
                .onGloballyPositioned {
                    width = it.size.width.toFloat()
                    widthLimit = width
                }
                .offset {
                    IntOffset(offsetYAnimate.roundToInt(), 0)
                }
                .draggable(
                    state = rememberDraggableState(
                        onDelta = {
                            xDrag += it

                            xDisplay = xDrag
                                .coerceAtMost(widthLimit)
                                .coerceAtLeast(-widthLimit)

                            offsetX = if (xDisplay < 0) {
                                xDisplay * (1 - offsetX / (-width))
                            } else {
                                xDisplay * (1 - offsetX / (width))
                            }
                        }
                    ),
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (offsetX.absoluteValue > trigger) {
                            if (offsetX > 0) {
                                onEpiIncrease()
                            } else {
                                onEpiDecrease()
                            }
                        }

                        offsetX = 0f
                        xDisplay = 0f
                        xDrag = 0f
                    }
                ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = entity.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        text = "${entity.currentEpisode}/${entity.totalEpisode} 集",
                        fontSize = 14.sp
                    )
                }

                CustomProgress(
                    modifier = Modifier.padding(10.dp),
                    currentValue = entity.currentEpisode,
                    maxValue = entity.totalEpisode,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    secondaryColor = MaterialTheme.colorScheme.onSecondary
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        onDateChange()
                                    }
                                )
                            },
                        text = entity.createTime.getTime(),
                        fontSize = 14.sp
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        text = entity.changeTime.getTime(),
                        fontSize = 14.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 30.dp),
            visible = showIcon == 1,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_exposure_plus_1_24),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "plus"
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp),
            visible = showIcon == 2,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_exposure_neg_1_24),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "neg"
            )
        }
    }
}