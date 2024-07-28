package com.anpe.bingewatch.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingItemMenu(
    modifier: Modifier = Modifier,
    title: String,
    menuContent: @Composable () -> Unit,
    summary: String? = null
) {
    var menuState by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                menuState = !menuState
            }
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            summary?.let {
                Text(
                    text = summary,
                    fontSize = 15.sp
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 15.dp)) {
            DropdownMenu(
                expanded = menuState,
                onDismissRequest = {
                    menuState = false
                }
            ) {
                menuContent()
            }
        }
    }
}