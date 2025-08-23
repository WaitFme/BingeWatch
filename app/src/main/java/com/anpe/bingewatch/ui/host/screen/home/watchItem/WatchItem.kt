package com.anpe.bingewatch.ui.host.screen.home.watchItem

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.anpe.bingewatch.R
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.utils.Tools.Companion.getTime
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchItem(
    modifier: Modifier = Modifier,
    entity: WatchEntity,
    onLongPress: () -> Unit = {},
    increaseEpi: (id: Long) -> Unit = {},
    decreaseEpi: (id: Long) -> Unit = {},
    onVibrate: () -> Unit = {},
) {
    val state = rememberWaState()
    var widthLimit by remember { mutableFloatStateOf(0f) }

    val offsetXAnimate by animateFloatAsState(targetValue = state.offsetX, label = "offsetX")

    val trigger by remember { derivedStateOf { state.size.width * 0.25f } }

    // 手势方向状态
    val swipeDirection by remember {
        derivedStateOf {
            when {
                state.offsetX > trigger -> SwipeDirection.RIGHT
                state.offsetX < -trigger -> SwipeDirection.LEFT
                else -> SwipeDirection.NONE
            }
        }
    }
    LaunchedEffect(swipeDirection) {
        if (swipeDirection != SwipeDirection.NONE && swipeDirection != state.lastVibrated) {
            onVibrate()
            state.lastVibrated = swipeDirection
        } else if (swipeDirection == SwipeDirection.NONE) {
            state.lastVibrated = SwipeDirection.NONE
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = entity.currentEpisode / entity.totalEpisode.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "progress"
    )

    Box(modifier = modifier) {
        val density = LocalDensity.current

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 30.dp),
            visible = swipeDirection == SwipeDirection.RIGHT,
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
            visible = swipeDirection == SwipeDirection.LEFT,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_exposure_neg_1_24),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "neg"
            )
        }

        WatchCard(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        onVibrate()
                        onLongPress()
                    })
                }
                .onGloballyPositioned {
                    state.size = it.size
                    widthLimit = state.size.width.toFloat()
                }
                .offset { IntOffset(offsetXAnimate.roundToInt(), 0) },
            title = entity.title,
            currentEpisode = entity.currentEpisode,
            totalEpisode = entity.totalEpisode,
            createTime = entity.createTime,
            changeTime = entity.changeTime,
            animatedProgress = animatedProgress,
        )

        Spacer(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(density.run { state.size.height.toDp() })
                .width(density.run { (state.size.width * 0.3f).toDp() })
                .draggable(
                    state = rememberDraggableState {
                        state.dragX += it

                        val xLimit = state.dragX.coerceIn(0f, widthLimit)

                        state.offsetX = xLimit * (1 - state.offsetX / (state.size.width))
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (state.offsetX.absoluteValue > trigger) {
                            increaseEpi(entity.id)
                        }

                        state.offsetX = 0f
                        state.dragX = 0f
                    }
                )
        )

        Spacer(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(density.run { state.size.height.toDp() })
                .width(density.run { (state.size.width * 0.3f).toDp() })
                .draggable(
                    state = rememberDraggableState {
                        state.dragX += it

                        val xLimit = state.dragX.coerceIn(-widthLimit, 0f)

                        state.offsetX = xLimit * (1 - state.offsetX / (-state.size.width))
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (state.offsetX.absoluteValue > trigger) {
                            decreaseEpi(entity.id)
                        }

                        state.offsetX = 0f
                        state.dragX = 0f
                    }
                )
        )
    }
}

@Composable
private fun WatchCard(
    modifier: Modifier = Modifier,
    title: String,
    currentEpisode: Int,
    totalEpisode: Int,
    createTime: Long,
    changeTime: Long,
    animatedProgress: Float
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp)
    ) {
        WatchItemContent(
            title = title,
            currentEpisode = currentEpisode,
            totalEpisode = totalEpisode,
            createTime = createTime,
            changeTime = changeTime,
            animatedProgress = animatedProgress
        )
    }
}

@Composable
private fun WatchItemContent(
    title: String,
    currentEpisode: Int,
    totalEpisode: Int,
    createTime: Long,
    changeTime: Long,
    animatedProgress: Float
) {
    ConstraintLayout {
        val (titleRef, episodeRef, progressRef, createTimeRef, changeTimeRef) = createRefs()

        Text(
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start, 20.dp)
                top.linkTo(parent.top, 20.dp)
            },
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.constrainAs(episodeRef) {
                top.linkTo(titleRef.top)
                end.linkTo(parent.end, 20.dp)
            },
            text = "${currentEpisode}/${totalEpisode} 集",
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
            modifier = Modifier.constrainAs(createTimeRef) {
                start.linkTo(parent.start, 20.dp)
                top.linkTo(progressRef.bottom, 10.dp)
                bottom.linkTo(parent.bottom, 20.dp)
            },
            text = createTime.getTime(),
            fontSize = 14.sp
        )

        Text(
            modifier = Modifier.constrainAs(changeTimeRef) {
                top.linkTo(createTimeRef.top)
                end.linkTo(parent.end, 20.dp)
                bottom.linkTo(parent.bottom, 20.dp)
            },
            text = changeTime.getTime(),
            fontSize = 14.sp
        )
    }
}

enum class SwipeDirection {
    NONE, LEFT, RIGHT
}

class WaState() {
    var size by mutableStateOf(IntSize(0, 0))
    var dragX by mutableFloatStateOf(0f)
    var offsetX by mutableFloatStateOf(0f)
    var lastVibrated by mutableStateOf(SwipeDirection.NONE)
}

@Composable
private fun rememberWaState() = remember { WaState() }