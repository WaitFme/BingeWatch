package com.anpe.bingewatch.ui.host.screen.main

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.anpe.bingewatch.data.local.entity.WatchEntity
import com.anpe.bingewatch.data.local.entity.WatchNewEntity
import com.anpe.bingewatch.intent.event.MainEvent
import com.anpe.bingewatch.ui.host.manager.ScreenManager
import com.anpe.bingewatch.ui.host.pager.WatchingPager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navControllerScreen: NavHostController) {
    val viewModel: MainViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    val dataList by viewModel.watchFlow.collectAsState()

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    var tabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                swi = {
                    scope.launch {
                        tabIndex = it
//                        viewModel.channel.send(MainEvent.GetIndex(it))
                    }
                },
                navi = {
                    navControllerScreen.navigate(ScreenManager.SettingsScreen.route) {
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
    ) { pv->
        Box(modifier = Modifier.padding(pv)) {
            WatchingPager(
                modifier = Modifier.fillMaxSize(),
                tabIndex = tabIndex,
                dataList = dataList,
                onUpdate = {
                    viewModel.updateWatch(it)
                },
                onDelete = {
                    viewModel.deleteWatch(it)
                }
            )
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
        ) {
            val context = LocalContext.current
            val keyboard = LocalSoftwareKeyboardController.current

            BackHandler {
                openBottomSheet = false
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            ) {
                var title by remember { mutableStateOf(TextFieldValue()) }
                val titleError by viewModel.watchTitleIsAlive.collectAsState()

                var currentEpisode by remember { mutableStateOf(TextFieldValue()) }
                var currentEpisodeError by remember { mutableStateOf(false) }

                var allEpisode by remember { mutableStateOf(TextFieldValue()) }
                var allEpisodeError by remember { mutableStateOf(false) }

                val checkState = remember { mutableStateOf(false) }

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

                        viewModel.findWatchAlive(title = title.text)
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
                    value = allEpisode,
                    isError = allEpisodeError,
                    label = {
                        Text(
                            text = if (allEpisodeError) stringResource(id = R.string.all_episode_tip) else stringResource(
                                id = R.string.all_episode
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = {
                        allEpisode = it

                        allEpisodeError =
                            try {
                                (allEpisode.text.toInt() == 0)
                            } catch (e: NumberFormatException) {
                                Log.d("TAG", "DialogContent: e")
                                allEpisode.text.isNotEmpty()
                            }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (title.text.isNotEmpty() && currentEpisode.text.isNotEmpty() && allEpisode.text.isNotEmpty()) {
                                if (titleError || currentEpisodeError || allEpisodeError) {
                                    Toast.makeText(
                                        context,
                                        "plz handle the error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@KeyboardActions
                                }

                                val time = System.currentTimeMillis()

                                val entity = WatchNewEntity(
                                    title = title.text,
                                    currentEpisode = currentEpisode.text.toInt(),
                                    totalEpisode = allEpisode.text.toInt(),
                                    watchState = if (currentEpisode.text.toInt() == 0) {
                                        1
                                    } else if (currentEpisode.text.toInt() == allEpisode.text.toInt()) {
                                        2
                                    } else {
                                        0
                                    },
                                    createTime = time,
                                    changeTime = time,
                                    remarks = "",
                                    isDelete = false
                                )

                                viewModel.insertWatch(entity)

                                if (checkState.value) {
                                    title = TextFieldValue("")
                                    currentEpisode = TextFieldValue("")
                                    allEpisode = TextFieldValue("")
                                } else {
                                    keyboard?.hide()
                                    openBottomSheet = false
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.input_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@KeyboardActions
                            }
                        }
                    )
                )

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(alignment = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.input_state))
                    Checkbox(
                        checked = checkState.value,
                        onCheckedChange = {
                            checkState.value = it
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(alignment = Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            openBottomSheet = false
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(all = 4.dp),
                            text = stringResource(id = R.string.cancel),
                            fontSize = 16.sp
                        )
                    }

                    TextButton(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        onClick = {
                            if (title.text.isNotEmpty() && currentEpisode.text.isNotEmpty() && allEpisode.text.isNotEmpty()) {
                                if (titleError || currentEpisodeError || allEpisodeError) {
                                    Toast.makeText(
                                        context,
                                        "plz handle the error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@TextButton
                                }

                                val time = System.currentTimeMillis()

                                val entity = WatchNewEntity(
                                    title = title.text,
                                    currentEpisode = currentEpisode.text.toInt(),
                                    totalEpisode = allEpisode.text.toInt(),
                                    watchState = if (currentEpisode.text.toInt() == 0) {
                                        1
                                    } else if (currentEpisode.text.toInt() == allEpisode.text.toInt()) {
                                        2
                                    } else {
                                        0
                                    },
                                    createTime = time,
                                    changeTime = time,
                                    remarks = "",
                                    isDelete = false
                                )

                                viewModel.insertWatch(entity)

                                if (checkState.value) {
                                    title = TextFieldValue("")
                                    currentEpisode = TextFieldValue("")
                                    allEpisode = TextFieldValue("")
                                } else {
                                    keyboard?.hide()
                                    openBottomSheet = false
                                }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(swi: (Int) -> Unit, navi: () -> Unit) {
    val window = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    val list = rememberSaveable {
        mutableListOf("追剧", "想看", "已看")
    }

    var label by rememberSaveable {
        mutableStateOf(list[0])
    }

    TopAppBar(
        title = {
            if (window == WindowWidthSizeClass.COMPACT) {
                Row {
                    list.forEachIndexed { index, s ->
                        InputChip(
                            modifier = Modifier.padding(0.dp, 5.dp, 10.dp, 5.dp),
                            selected = label == s,
                            onClick = {
                                if (label != s) {
                                    label = s
                                }
                                swi(index)
                            },
                            label = { Text(text = s) }
                        )
                    }
                }
            } else {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = label,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (window != WindowWidthSizeClass.COMPACT) {
                Row(
                    modifier = Modifier.padding(end = 5.dp)
                ) {
                    list.forEachIndexed { index, s ->
                        InputChip(
                            modifier = Modifier.padding(5.dp),
                            selected = label == s,
                            onClick = {
                                if (label != s) {
                                    label = s
                                }
                                swi(index)
                            },
                            label = { Text(text = s) }
                        )
                    }
                }
            }
            IconButton(
                modifier = Modifier.padding(end = 15.dp),
                onClick = { navi() }
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}