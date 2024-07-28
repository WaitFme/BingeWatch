package com.anpe.bingewatch.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingItemSwitch(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var switchChecked by remember {
        mutableStateOf(checked)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                switchChecked = !switchChecked
                onCheckedChange(switchChecked)
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

        Switch(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .align(Alignment.CenterEnd),
            checked = checked,
            onCheckedChange = {
                switchChecked = it
                onCheckedChange(it)
            }
        )
    }
}