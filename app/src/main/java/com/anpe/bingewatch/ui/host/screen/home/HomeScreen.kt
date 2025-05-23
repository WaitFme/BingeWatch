package com.anpe.bingewatch.ui.host.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.anpe.bingewatch.R
import com.anpe.bingewatch.ui.host.manage.ScreenManager
import com.anpe.bingewatch.ui.widget.MyDialog
import com.anpe.bingewatch.ui.widget.WatchItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navControllerScreen: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    val homeState by viewModel.homeState.collectAsStateWithLifecycle()

    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect {
            when (it) {
                HomeEvent.ShowDialog -> {
                    showDialog = true
                }

                HomeEvent.CloseDialog -> {
                    showDialog = false
                }

                HomeEvent.PopBack -> {
                    navControllerScreen.popBackStack()
                }

                is HomeEvent.NaviScreen -> {
                    navControllerScreen.navigate(it.route) {
                        popUpTo(navControllerScreen.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                is HomeEvent.ShowToast -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onSwitchTab = {
                    scope.launch {
                        viewModel.dispatch(HomeAction.ChangeTabIndex(it))
                    }
                },
                onNavigate = {
                    scope.launch {
                        viewModel.dispatch(HomeAction.NaviScreen(it))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    viewModel.dispatch(HomeAction.NaviScreen(ScreenManager.EditScreen.route))
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add data"
                )
            }
        },
    ) { pv ->
        pv
        LazyVerticalGrid(
            modifier = Modifier
                .padding(top = pv.calculateTopPadding())
                .fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 250.dp),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp)
        ) {
            homeState.data.forEach { entity ->
                if (homeState.selectTab == entity.watchState) {
                    item(key = entity.id) {
                        LaunchedEffect(viewModel.viewEvent) {
                            viewModel.viewEvent.collect {
                                if (it is HomeEvent.CloseDialog) showDialog = false
                            }
                        }

                        WatchItem(
                            modifier = Modifier
                                .animateItem()
                                .padding(5.dp),
                            entity = entity,
                            increaseEpi = {
                                scope.launch {
                                    viewModel.dispatch(HomeAction.IncreaseEpi(it))
                                }
                            },
                            decreaseEpi = {
                                scope.launch {
                                    viewModel.dispatch(HomeAction.DecreaseEpi(it))
                                }
                            },
                            onLongPress = {
                                scope.launch {
                                    viewModel.dispatch(HomeAction.ShowDialog(entity.id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        Dialog(
            title = homeState.title,
            currentEpi = homeState.currentEpi,
            totalEpi = homeState.totalEpi,
            errorMessage = homeState.errorMessage,
            onDismissRequest = { scope.launch { viewModel.dispatch(HomeAction.DismissDialog) } },
            onDelete = {
                scope.launch { viewModel.dispatch(HomeAction.DeleteData(homeState.id)) }
            },
            onUpdate = {
                scope.launch { viewModel.dispatch(HomeAction.UpdateData(homeState.id)) }
            },
            changeCEpi = {
                scope.launch { viewModel.dispatch(HomeAction.ChangeCurrentEpi(it)) }
            },
            changeTEpi = {
                scope.launch { viewModel.dispatch(HomeAction.ChangeTotalEpi(it)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onSwitchTab: (Int) -> Unit,
    onNavigate: (String) -> Unit
) {
    val window = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    val context = LocalContext.current

    val labels = rememberSaveable {
        mutableListOf(
            context.resources.getString(R.string.watching),
            context.resources.getString(R.string.want),
            context.resources.getString(R.string.watched)
        )
    }

    var selectLabel by rememberSaveable { mutableStateOf(labels[0]) }

    TopAppBar(
        modifier = modifier,
        title = {
            if (window == WindowWidthSizeClass.COMPACT) {
                Row {
                    labels.forEachIndexed { index, label ->
                        InputChip(
                            modifier = Modifier.padding(0.dp, 5.dp, 10.dp, 5.dp),
                            selected = selectLabel == label,
                            onClick = {
                                if (selectLabel != label) {
                                    selectLabel = label
                                }
                                onSwitchTab(index)
                            },
                            label = { Text(text = label) }
                        )
                    }
                }
            } else {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = selectLabel,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (window != WindowWidthSizeClass.COMPACT) {
                Row(
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    labels.forEachIndexed { index, label ->
                        InputChip(
                            modifier = Modifier.padding(5.dp),
                            selected = selectLabel == label,
                            onClick = {
                                if (selectLabel != label) {
                                    selectLabel = label
                                }
                                onSwitchTab(index)
                            },
                            label = { Text(text = label) }
                        )
                    }
                }
            }
            IconButton(
                modifier = Modifier.padding(end = 5.dp),
                onClick = { onNavigate(ScreenManager.SettingsScreen.route) }
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
private fun Dialog(
    title: String,
    currentEpi: TextFieldValue,
    totalEpi: TextFieldValue,
    errorMessage: String,
    onDismissRequest: () -> Unit,
    changeCEpi: (TextFieldValue) -> Unit = {},
    changeTEpi: (TextFieldValue) -> Unit = {},
    onDelete: () -> Unit = {},
    onUpdate: () -> Unit = {},
) {
    MyDialog(
        title = title,
        onDismissRequest = onDismissRequest,
        content = {
            OutlinedTextField(
                value = currentEpi,
                label = { Text(text = stringResource(R.string.current_episode)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                onValueChange = changeCEpi
            )

            OutlinedTextField(
                value = totalEpi,
                label = { Text(text = stringResource(R.string.total_episode)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = changeTEpi,
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        deleteButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(text = stringResource(id = R.string.delete), color = Color.Red)
            }
        },
        updateButton = {
            TextButton(
                onClick = onUpdate
            ) {
                Text(text = stringResource(id = R.string.update))
            }
        }
    )
}
