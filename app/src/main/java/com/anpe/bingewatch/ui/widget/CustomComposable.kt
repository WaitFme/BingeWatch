package com.anpe.bingewatch.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun SettingItem1(
    modifier: Modifier = Modifier,
    title: String,
    onClick: (() -> Unit?)? = null,
    summary: String? = null,
    switchKey: String? = null
) {
    val context = LocalContext.current

    val sp = context.getSharedPreferences("${context.packageName}_settings", 0)

    var switchChecked by remember {
        mutableStateOf( if (switchKey != null) sp.getBoolean(switchKey, false) else false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                switchKey?.let {
                    switchChecked = !switchChecked
                }
                onClick?.let {
                    onClick()
                }
            }
            .height(70.dp)
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
                    fontSize = 16.sp
                )
            }
        }

        switchKey?.let {
            Switch(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .align(Alignment.CenterEnd),
                checked = switchChecked,
                onCheckedChange = {
                    switchChecked = it
                    sp.edit().putBoolean(switchKey, switchChecked).apply()
                }
            )
        }
    }
}