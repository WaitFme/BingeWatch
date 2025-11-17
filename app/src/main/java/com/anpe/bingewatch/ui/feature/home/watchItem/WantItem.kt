package com.anpe.bingewatch.ui.feature.home.watchItem

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.anpe.bingewatch.core.data.entity.WatchEntity
import com.anpe.bingewatch.utils.Tools.Companion.getTime

@Composable
fun WantItem(
    modifier: Modifier = Modifier,
    entity: WatchEntity,
    onLongPress: () -> Unit = {},
    play: (id: Long) -> Unit = {},
    onVibrate: () -> Unit = {},
) {
    Box(modifier = modifier) {
        WantCard(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        onVibrate()
                        onLongPress()
                    })
                },
            title = entity.title,
            currentEpisode = entity.currentEpisode,
            totalEpisode = entity.totalEpisode,
            createTime = entity.createTime,
            changeTime = entity.changeTime,
        )
    }
}

@Composable
fun WantCard(
    modifier: Modifier = Modifier,
    title: String,
    currentEpisode: Int,
    totalEpisode: Int,
    createTime: Long,
    changeTime: Long,
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
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (titleRef, episodeRef, typeRef, createTimeRef, changeTimeRef, playRef) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(titleRef) {
                    start.linkTo(parent.start, 15.dp)
                    top.linkTo(parent.top, 15.dp)
                },
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

//        Text(
//            modifier = Modifier.constrainAs(episodeRef) {
//                top.linkTo(titleRef.top)
//                end.linkTo(parent.end, 20.dp)
//            },
//            text = "${currentEpisode}/${totalEpisode} 集",
//            fontSize = 14.sp
//        )

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(7.5.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(3.dp, 0.dp, 3.dp, 0.dp)
                .constrainAs(typeRef) {
                    top.linkTo(titleRef.top)
                    end.linkTo(parent.end, 15.dp)
                },
            text = if (totalEpisode == 1) "电影" else "电视剧",
            fontSize = 8.sp
        )

        Text(
            modifier = Modifier.constrainAs(createTimeRef) {
                start.linkTo(parent.start, 15.dp)
                top.linkTo(titleRef.bottom, 10.dp)
                bottom.linkTo(parent.bottom, 15.dp)
            },
            text = createTime.getTime(),
            fontSize = 14.sp
        )

        /*Text(
            modifier = Modifier.constrainAs(changeTimeRef) {
                top.linkTo(createTimeRef.top)
                end.linkTo(parent.end, 15.dp)
                bottom.linkTo(parent.bottom, 15.dp)
            },
            text = changeTime.getTime(),
            fontSize = 14.sp
        )*/

        IconButton(
            modifier = Modifier
                .size(25.dp)
                .constrainAs(playRef) {
                    top.linkTo(createTimeRef.top)
                    end.linkTo(parent.end, 15.dp)
                    bottom.linkTo(parent.bottom, 15.dp)
                }, onClick = {
//                play(entity.id)
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = "play")
        }
    }
}