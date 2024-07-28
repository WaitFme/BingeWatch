package com.anpe.bingewatch.ui.host.pager

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anpe.bingewatch.R
import com.anpe.bingewatch.data.local.entity.WatchEntity
import com.anpe.bingewatch.data.local.entity.WatchNewEntity
import com.anpe.bingewatch.ui.widget.CustomProgress
import com.anpe.bingewatch.ui.widget.MyDialog
import com.anpe.bingewatch.utils.Tools.Companion.getTime
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun WatchingPager(
    modifier: Modifier = Modifier,
    tabIndex: Int,
    dataList: List<WatchNewEntity>,
    onUpdate: (WatchNewEntity) -> Unit,
    onDelete: (WatchNewEntity) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 250.dp),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp)
    ) {
        items(count = dataList.size, key = { dataList[it].id }) {
            if (tabIndex == dataList[it].watchState) {
                CardItem(
                    modifier = Modifier
                        .animateItem()
                        .padding(5.dp),
                    entity = dataList[it],
                    onUpdate = onUpdate,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    entity: WatchNewEntity,
    onUpdate: (WatchNewEntity) -> Unit,
    onDelete: (WatchNewEntity) -> Unit,
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
    var widthlimit by remember { mutableFloatStateOf(0f) }

    var showDialog by remember {
        mutableStateOf(false)
    }

    val showIcon = if (offsetX > width * 0.30) {
        1
    } else if (offsetX < -(width * 0.30)) {
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
                            showDialog = !showDialog

                            scope.launch {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    vibrator.vibrate(50)
                                }
                            }
                        }
                    )
                }
                .onGloballyPositioned {
                    width = it.size.width.toFloat()
//                    widthlimit = width * 2 / 3
                    widthlimit = width
                }
                .offset {
                    IntOffset(offsetYAnimate.roundToInt(), 0)
                }
                .draggable(
                    state = rememberDraggableState(
                        onDelta = {
                            xDrag += it

                            xDisplay = xDrag
                                .coerceAtMost(widthlimit)
                                .coerceAtLeast(-widthlimit)

                            offsetX = if (xDisplay < 0) {
                                xDisplay * (1 - offsetX / (-width))
                            } else {
                                xDisplay * (1 - offsetX / (width))
                            }
                        }
                    ),
                    orientation = Orientation.Horizontal,
                    onDragStarted = {

                    },
                    onDragStopped = {
                        if (offsetX.absoluteValue > width / 3) {
                            val time = System.currentTimeMillis()

                            val currentEpisode =
                                if (offsetX > 0 && entity.currentEpisode < entity.totalEpisode) {
                                    entity.currentEpisode + 1
                                } else if (offsetX < 0 && entity.currentEpisode > 0) {
                                    entity.currentEpisode - 1
                                } else {
                                    entity.currentEpisode
                                }

                            val watchingState: Int = when (currentEpisode) {
                                0 -> 1
                                entity.totalEpisode -> 2
                                else -> 0
                            }

                            val watchEntity = WatchNewEntity(
                                title = entity.title,
                                currentEpisode = currentEpisode,
                                totalEpisode = entity.totalEpisode,
                                watchState = watchingState,
                                createTime = entity.createTime,
                                changeTime = time,
                                isDelete = entity.isDelete,
                                remarks = entity.remarks
                            )

                            onUpdate(watchEntity)
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
                imageVector = Icons.Default.Add,
                contentDescription = ""
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
                imageVector = Icons.Default.Close,
                contentDescription = ""
            )
        }
    }

    if (showDialog) {
        var currentEpisode by remember {
            mutableStateOf(TextFieldValue(text = entity.currentEpisode.toString()))
        }
        var allEpisode by remember {
            mutableStateOf(TextFieldValue(text = entity.totalEpisode.toString()))
        }

        val isError = remember {
            mutableStateListOf(false, false)
        }

        val label = remember {
            mutableStateListOf(
                context.resources.getString(if (isError[0]) R.string.current_episode_tip else R.string.current_episode),
                context.resources.getString(if (isError[1]) R.string.all_episode_tip else R.string.all_episode)
            )
        }

        MyDialog(
            title = entity.title,
            onDismissRequest = { showDialog = false },
            content = {
                OutlinedTextField(
                    value = currentEpisode,
                    isError = isError[0],
                    label = { Text(text = label[0]) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {
                        currentEpisode = it
                        isError[0] = try {
                            currentEpisode.text.toInt()
                            false
                        } catch (e: NumberFormatException) {
                            currentEpisode.text.isNotEmpty()
                        }
                    }
                )

                OutlinedTextField(
                    value = allEpisode,
                    isError = isError[1],
                    label = { Text(text = label[1]) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = {
                        allEpisode = it
                        isError[1] = try {
                            (allEpisode.text.toInt() == 0)
                        } catch (e: NumberFormatException) {
                            allEpisode.text.isNotEmpty()
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("取消")
                }
            },
            deleteButton = {
                TextButton(
                    onClick = {
                        onDelete(entity)
                        showDialog = false
                    }
                ) {
                    Text(text = "删除", color = Color.Red)
                }
            },
            updateButton = {
                TextButton(
                    onClick = {
                        if (currentEpisode.text.isNotEmpty() && allEpisode.text.isNotEmpty()) {
                            if (isError[0] || isError[1]) {
                                Toast.makeText(
                                    context,
                                    "plz handle the error",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@TextButton
                            }

                            val watchingState = if (currentEpisode.text.toInt() == 0) {
                                1
                            } else if (currentEpisode.text.toInt() == allEpisode.text.toInt()) {
                                2
                            } else {
                                0
                            }

                            onUpdate(
                                WatchNewEntity(
                                    title = entity.title,
                                    currentEpisode = currentEpisode.text.toInt(),
                                    totalEpisode = allEpisode.text.toInt(),
                                    watchState = watchingState,
                                    createTime = entity.createTime,
                                    changeTime = System.currentTimeMillis(),
                                    remarks = entity.remarks,
                                    isDelete = entity.isDelete
                                )
                            )

                            showDialog = false
                        } else {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.input_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                    }
                ) {
                    Text(text = "更新")
                }
            }
        )
    }
}