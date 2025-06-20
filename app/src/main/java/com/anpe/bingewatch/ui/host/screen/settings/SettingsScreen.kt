package com.anpe.bingewatch.ui.host.screen.settings

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.ui.widget.SettingItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel: SettingsViewModel = hiltViewModel()

    val context = LocalContext.current

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect {
            when (it) {
                SettingsEvent.PopBack -> navController.popBackStack()
                is SettingsEvent.Toast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Surface {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Settings") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    }
                )
            },
            content = {
                SettingContent(
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding()),
                    viewModel = viewModel
                )
            }
        )
    }
}

@Composable
fun SettingContent(modifier: Modifier, viewModel: SettingsViewModel) {
    Column(
        modifier = modifier
    ) {
        val settingsState by viewModel.viewState.collectAsState()
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        var expanded by remember { mutableStateOf(false) }
        var serverDialog by remember { mutableStateOf(false) }
        var deleteDialog by remember { mutableStateOf(false) }

        // 将json文件内容导入到数据库
        val openSelectPhotoLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                val contentResolver = context.contentResolver

                uri?.let {
                    // 使用 ContentResolver 打开输入流
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        // 使用 Moshi 解析 JSON 数据
                        val moshi =
                            Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                        val jsonAdapter = moshi.adapter<List<WatchEntity>>(
                            Types.newParameterizedType(
                                List::class.java,
                                WatchEntity::class.java
                            )
                        )

                        // 从输入流读取 JSON 内容
                        val jsonString = inputStream.bufferedReader().use { it.readText() }

                        // 解析 JSON 为 WatchEntity 对象列表
                        val watchEntities = jsonAdapter.fromJson(jsonString)

                        // 检查解析是否成功并将数据插入到数据库
                        if (watchEntities != null) {
                            viewModel.uos(*watchEntities.map { it }.toTypedArray())

                            Toast.makeText(
                                context,
                                "导入成功!${watchEntities[0].title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "解析 JSON 失败!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        )

        // 将数据库数据转成json文件保存到手机
        val openSelectPhotoLauncherSave = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json"),
            onResult = {
                val contentResolver = context.contentResolver
                val outputStream = contentResolver.openOutputStream(it!!)

                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val pt =
                    Types.newParameterizedType(List::class.java, WatchEntity::class.java)

                val jsonAdapter = moshi.adapter<List<WatchEntity>>(pt)
                val toJson = jsonAdapter.toJson(settingsState.data)
                outputStream?.use { stream ->
                    stream.write(toJson.toString().toByteArray())
                    stream.flush()
                }
            }
        )

        SettingItem(
            title = "同步服务器",
            summary = {
                Text(
                    text = if (settingsState.serverAddress.isEmpty()) {
                        "设置服务器地址"
                    } else {
                        settingsState.serverAddress
                    },
                    fontSize = 15.sp
                )

                if (serverDialog) {
                    AlertDialog(
                        title = {
                            OutlinedTextField(
                                value = settingsState.serverAddress,
                                label = { Text(text = "设置服务器地址") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                onValueChange = {
                                    scope.launch {
                                        viewModel.dispatch(
                                            SettingsAction.ChangeServerAddress(
                                                it.toString()
                                            )
                                        )
                                    }
                                },
                                keyboardActions = KeyboardActions(onDone = {
                                    serverDialog = false
                                    Toast.makeText(context, "set success", Toast.LENGTH_SHORT)
                                        .show()
                                })
                            )
                        },
                        onDismissRequest = {
                            serverDialog = false
                        },
                        confirmButton = {
                            Button(onClick = {
                                serverDialog = false
                            }) {
                                Text(text = "Apply")
                            }
                        }
                    )
                }
            },
            onClick = { serverDialog = true }
        )
        SettingItem(title = "导出数据", summary = "导出数据到Json", onClick = {
            scope.launch {
                viewModel.dispatch(SettingsAction.ExportData)
            }
            val time = System.currentTimeMillis()
            openSelectPhotoLauncherSave.launch("Watch_bak_$time.json")
        })
        SettingItem(title = "导入数据", summary = "从磁盘导入Json数据", onClick = {
            openSelectPhotoLauncher.launch("application/json")
        })
        SettingItem(
            title = "排序方式",
            summary = {
                val sortMaps = mapOf<Int, String>(
                    0 to "按照标题排序",
                    1 to "按照创建时间排序",
                    2 to "按照修改时间排序",
                )

                Text(
                    text = sortMaps[settingsState.sortType]?:"ERROR",
                    fontSize = 15.sp
                )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    sortMaps.forEach {
                        DropdownMenuItem(
                            text = { Text(it.value) },
                            onClick = {
                                scope.launch {
                                    viewModel.dispatch(SettingsAction.ChangeSortType(it.key))
                                }
                                expanded = false
                            }
                        )
                    }
                }
            },
            onClick = {
                expanded = true
            }
        )
        SettingItem(
            title = "清空数据",
            summary = {
                Text("清空所有数据", fontSize = 15.sp)

                if (deleteDialog) {
                    AlertDialog(
                        title = {
                            Text(text = "是否要清除所有数据？")
                        },
                        onDismissRequest = {
                            deleteDialog = false
                        },
                        dismissButton = {
                            Button(onClick = {
                                deleteDialog = false
                            }) {
                                Text(text = "CANCEL")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                scope.launch {
                                    viewModel.dispatch(SettingsAction.ClearData)
                                    Log.d("SettingsScreen", "SettingContent: Clear")
                                }
                                Toast.makeText(context, "delete success", Toast.LENGTH_SHORT).show()
                                deleteDialog = false
                            }) {
                                Text(text = "DELETE")
                            }
                        }
                    )
                }
            },
            onClick = { deleteDialog = true }
        )
    }
}