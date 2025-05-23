package com.anpe.bingewatch.ui.widget

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.anpe.bingewatch.R
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.utils.Tools.Companion.getTime
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchItem(
    modifier: Modifier = Modifier,
    entity: WatchEntity,
    onLongPress: () -> Unit = {},
    increaseEpi: (id: Long) -> Unit = {},
    decreaseEpi: (id: Long) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var width by remember { mutableFloatStateOf(0f) }
    var height by remember { mutableFloatStateOf(0f) }
    var widthLimit by remember { mutableFloatStateOf(0f) }

    var xDrag by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    val offsetXAnimate by animateFloatAsState(targetValue = offsetX, label = "offsetX")

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
                            onLongPress()
                        }
                    )
                }
                .onGloballyPositioned {
                    width = it.size.width.toFloat()
                    height = it.size.height.toFloat()
                    widthLimit = width
                }
                .offset {
                    IntOffset(offsetXAnimate.roundToInt(), 0)
                },
            shape = RoundedCornerShape(15.dp)
        ) {
            ConstraintLayout {
                val (titleRef, episodeRef, progressRef, createTimeRef, changeTimeRef) = createRefs()

                val animatedProgress by animateFloatAsState(
                    targetValue = entity.currentEpisode / entity.totalEpisode.toFloat(),
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = "progress"
                )

                Text(
                    modifier = Modifier.constrainAs(titleRef) {
                        start.linkTo(parent.start, 20.dp)
                        top.linkTo(parent.top, 20.dp)
                    },
                    text = entity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.constrainAs(episodeRef) {
                        top.linkTo(titleRef.top)
                        end.linkTo(parent.end, 20.dp)
                    },
                    text = "${entity.currentEpisode}/${entity.totalEpisode} 集",
                    fontSize = 14.sp
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .constrainAs(progressRef) {
                            start.linkTo(parent.start, 20.dp)
                            top.linkTo(titleRef.bottom, 10.dp)
                            end.linkTo(parent.end, 20.dp)
                            this.width = Dimension.preferredWrapContent
                        },
                    progress = {
                        animatedProgress
                    },
                    trackColor = MaterialTheme.colorScheme.onSecondary
                )

                Text(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                }
                            )
                        }
                        .constrainAs(createTimeRef) {
                            start.linkTo(parent.start, 20.dp)
                            top.linkTo(progressRef.bottom, 10.dp)
                            bottom.linkTo(parent.bottom, 20.dp)
                        },
                    text = entity.createTime.getTime(),
                    fontSize = 14.sp
                )

                Text(
                    modifier = Modifier.constrainAs(changeTimeRef) {
                        top.linkTo(createTimeRef.top)
                        end.linkTo(parent.end, 20.dp)
                        bottom.linkTo(parent.bottom, 20.dp)
                    },
                    text = entity.changeTime.getTime(),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(LocalDensity.current.run { height.toDp() })
                .width(LocalDensity.current.run { (width * 0.3f).toDp() })
                .draggable(
                    state = rememberDraggableState {
                        xDrag += it

                        val xLimit = xDrag
                            .coerceAtMost(widthLimit)
                            .coerceAtLeast(0f)

                        offsetX = xLimit * (1 - offsetX / (width))
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (offsetX.absoluteValue > trigger) {
                            increaseEpi(entity.id)
                        }

                        offsetX = 0f
                        xDrag = 0f
                    }
                ),
        )

        Spacer(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(LocalDensity.current.run { height.toDp() })
                .width(LocalDensity.current.run { (width * 0.3f).toDp() })
                .draggable(
                    state = rememberDraggableState {
                        xDrag += it

                        val xLimit = xDrag
                            .coerceAtMost(0f)
                            .coerceAtLeast(-widthLimit)

                        offsetX = xLimit * (1 - offsetX / (-width))
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (offsetX.absoluteValue > trigger) {
                            decreaseEpi(entity.id)
                        }

                        offsetX = 0f
                        xDrag = 0f
                    }
                )
        )
    }
}