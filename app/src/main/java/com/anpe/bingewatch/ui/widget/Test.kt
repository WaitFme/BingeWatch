package com.anpe.bingewatch.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Test(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    itemList: List<String>,
    onClick: (Int) -> Unit = {}
) {
    var menuState by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                menuState = !menuState
            }
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

            Text(
                text = summary,
                fontSize = 15.sp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
        ) {
            DropdownMenu(
                modifier = Modifier,
                expanded = menuState,
                onDismissRequest = {
                    menuState = false
                }
            ) {
                for ((index, string) in itemList.withIndex()) {
                    DropdownMenuItem(text = { Text(text = string) }, onClick = {
                        onClick(index)
                        menuState = false
                    })
                }
            }
        }
    }
}