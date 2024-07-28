package com.anpe.bingewatch.ui.widget

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    railBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
    sheetContent: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    configuration: Configuration,
    changeValue: Dp? = null,
    visible: Boolean,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    onDismissRequest: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row {
            if (changeValue != null) {
                if (configuration.screenWidthDp.dp >= changeValue) {
                    railBar()
                }
            }
            Scaffold(
                modifier = modifier,
                topBar = {
                    topBar()
                },
                content = {
                    Column(
                        modifier = Modifier.padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding()
                        )
                    ) {
                        content()
                    }
                },
                floatingActionButton = {
                    floatingActionButton()
                },
                bottomBar = {
                    if (changeValue == null) {
                        bottomBar()
                    } else {
                        if (configuration.screenWidthDp.dp < changeValue) {
                            bottomBar()
                        }
                    }
                }
            )
        }

        BottomSheetDialog(
            modifier = Modifier,
            visible = visible,
            cancelable = cancelable,
            canceledOnTouchOutside = canceledOnTouchOutside,
            onDismissRequest = onDismissRequest,
            content = {
                sheetContent()
            }
        )
    }
}