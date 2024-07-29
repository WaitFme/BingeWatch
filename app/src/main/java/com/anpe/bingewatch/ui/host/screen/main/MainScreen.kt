package com.anpe.bingewatch.ui.host.screen.main

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowWidthSizeClass
import com.anpe.bingewatch.R
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.ui.host.manage.ScreenManager
import com.anpe.bingewatch.ui.widget.MyDialog
import com.anpe.bingewatch.ui.widget.WatchItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navControllerScreen: NavHostController) {
    val viewModel: MainViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    val entities by viewModel.watchFlow.collectAsState()

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    var tabIndex by remember { mutableIntStateOf(0) }

    var modEntity by rememberSaveable {
        mutableStateOf<WatchEntity?>(null)
    }

    var showPicker by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopBar(
                onSwitchTab = {
                    scope.launch {
                        tabIndex = it
                    }
                },
                onNavigate = {
                    navControllerScreen.navigate(it) {
                        popUpTo(navControllerScreen.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openBottomSheet = !openBottomSheet
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add data"
                )
            }
        },
    ) { pv ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(top = pv.calculateTopPadding())
                .fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 250.dp),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp)
        ) {
            entities.forEach { entity ->
                if (tabIndex == entity.watchState) {
                    item(key = entity.id) {
                        WatchItem(
                            modifier = Modifier
                                .animateItem()
                                .padding(5.dp),
                            entity = entity,
                            onDateChange = {
                                showPicker = true
                            },
                            onDialog = {
                                scope.launch {
                                    viewModel.channel.send(MainIntent.UpdateCurrentWatch(entity.id))
                                    showDialog = true
                                }
                            },
                            onEpiIncrease = {
                                scope.launch {
                                    viewModel.channel.send(MainIntent.EpiIncrease(entity.id))
                                }
                            },
                            onEpiDecrease = {
                                scope.launch {
                                    viewModel.channel.send(MainIntent.EpiDecrease(entity.id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier)

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
//            initialSelectedDateMillis = entity.createTime,
            initialDisplayMode = DisplayMode.Picker
        )

        DatePickerDialog(
            modifier = Modifier
//                .padding(15.dp)
//                .width(300.dp)
//                .clip(RoundedCornerShape(30.dp))
//                .background(MaterialTheme.colorScheme.surfaceVariant)
            ,
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                        snackScope.launch {
                            snackState.showSnackbar(
                                "Selected date timestamp: ${datePickerState.selectedDateMillis}"
                            )
                        }
                    },
                    enabled = true
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(
                modifier = Modifier
                    .padding(15.dp)
                    .width(200.dp),
                state = datePickerState
            )
        }
    }

    if (showDialog) {
        DialogBlock(
            onDismissRequest = { showDialog = false },
            viewModel = viewModel
        )
    }

    if (openBottomSheet) {
        BottomSheet(
            onDismissRequest = { openBottomSheet = false },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
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
private fun DialogBlock(
    onDismissRequest: () -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    val currentWatch by viewModel.currentWatchFlow.collectAsState()

    if (currentWatch == null) {
        return
    }

    val scope = rememberCoroutineScope()

    var currentEpisode by remember {
        mutableStateOf(TextFieldValue(text = currentWatch?.currentEpisode.toString()))
    }
    var allEpisode by remember {
        mutableStateOf(TextFieldValue(text = currentWatch?.totalEpisode.toString()))
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
        title = currentWatch?.title!!,
        onDismissRequest = onDismissRequest,
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
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        deleteButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        viewModel.channel.send(MainIntent.DeleteWatch(currentWatch?.id!!))
                    }
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(id = R.string.delete), color = Color.Red)
            }
        },
        updateButton = {
            TextButton(
                onClick = {
                    if (currentEpisode.text.isNotEmpty() && allEpisode.text.isNotEmpty()) {
                        if (isError[0] || isError[1]) {
                            Toast.makeText(context, R.string.type_error, Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        scope.launch {
                            viewModel.channel.send(MainIntent.UpdateWatch(
                                id = currentWatch?.id!!,
                                currentEpi = currentEpisode.text.toInt(),
                                totalEpi = allEpisode.text.toInt(),
                                watchState = -1
                            ))
                        }

                        onDismissRequest()
                    } else {
                        Toast.makeText(context, R.string.input_error, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.update))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(
    onDismissRequest: () -> Unit,
    viewModel: MainViewModel
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
    ) {
        BackHandler {
            onDismissRequest()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {
            val context = LocalContext.current

            val scope = rememberCoroutineScope()

            val titleAlive by viewModel.watchTitleIsAlive.collectAsState()

            var title by remember { mutableStateOf(TextFieldValue()) }
            var currentEpisode by remember { mutableStateOf(TextFieldValue()) }
            var totalEpisode by remember { mutableStateOf(TextFieldValue()) }

            val titleError = titleAlive
            var currentEpisodeError by remember { mutableStateOf(false) }
            var totalEpisodeError by remember { mutableStateOf(false) }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                isError = titleError,
                label = {
                    Text(
                        text = if (titleError) stringResource(id = R.string.tv_name_tip) else
                            stringResource(id = R.string.tv_name)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onValueChange = {
                    title = it

                    scope.launch {
                        if (title.text.isNotEmpty()) {
                            viewModel.channel.send(MainIntent.FindTitleAlive(title.text))
                        }
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentEpisode,
                isError = currentEpisodeError,
                label = {
                    Text(
                        text = if (currentEpisodeError) stringResource(id = R.string.current_episode_tip) else stringResource(
                            id = R.string.current_episode
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                onValueChange = {
                    currentEpisode = it

                    currentEpisodeError = try {
                        currentEpisode.text.toInt()
                        false
                    } catch (e: NumberFormatException) {
                        Log.d("TAG", "DialogContent: e")
                        currentEpisode.text.isNotEmpty()
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = totalEpisode,
                isError = totalEpisodeError,
                label = {
                    Text(
                        text = if (totalEpisodeError) stringResource(id = R.string.all_episode_tip) else stringResource(
                            id = R.string.all_episode
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = {
                    totalEpisode = it

                    totalEpisodeError =
                        try {
                            (totalEpisode.text.toInt() == 0)
                        } catch (e: NumberFormatException) {
                            Log.d("TAG", "DialogContent: e")
                            totalEpisode.text.isNotEmpty()
                        }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.text.isNotEmpty() && currentEpisode.text.isNotEmpty() && totalEpisode.text.isNotEmpty()) {
                            if (titleError || currentEpisodeError || totalEpisodeError) {
                                Toast.makeText(context, R.string.type_error, Toast.LENGTH_SHORT)
                                    .show()
                                return@KeyboardActions
                            }

                            scope.launch {
                                viewModel.channel.send(
                                    MainIntent.CreateWatch(
                                        title = title.text,
                                        remarks = "",
                                        currentEpi = currentEpisode.text.toInt(),
                                        totalEpi = totalEpisode.text.toInt()
                                    )
                                )
                            }

                            onDismissRequest()
                        } else {
                            Toast.makeText(context, R.string.input_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(alignment = Alignment.End)
            ) {
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(
                        modifier = Modifier.padding(all = 4.dp),
                        text = stringResource(id = R.string.cancel),
                        fontSize = 16.sp
                    )
                }

                TextButton(
                    modifier = Modifier.padding(start = 5.dp),
                    onClick = {
                        if (title.text.isNotEmpty() && currentEpisode.text.isNotEmpty() && totalEpisode.text.isNotEmpty()) {
                            if (titleAlive || currentEpisodeError || totalEpisodeError) {
                                Toast.makeText(context, R.string.type_error, Toast.LENGTH_SHORT)
                                    .show()
                                return@TextButton
                            }

                            scope.launch {
                                viewModel.channel.send(
                                    MainIntent.CreateWatch(
                                        title = title.text,
                                        remarks = "",
                                        currentEpi = currentEpisode.text.toInt(),
                                        totalEpi = totalEpisode.text.toInt()
                                    )
                                )
                            }

                            onDismissRequest()
                        } else {
                            Toast.makeText(context, R.string.input_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(all = 4.dp),
                        text = stringResource(id = R.string.apply),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
