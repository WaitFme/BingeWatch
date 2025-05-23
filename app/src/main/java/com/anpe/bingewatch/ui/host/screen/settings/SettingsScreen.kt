package com.anpe.bingewatch.ui.host.screen.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.ui.host.screen.home.HomeViewModel
import com.anpe.bingewatch.ui.widget.SettingItem
import com.anpe.bingewatch.ui.widget.Test
import com.anpe.bingewatch.utils.Tools
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel: SettingsViewModel = hiltViewModel()

    val settingsState by viewModel.settingsState.collectAsState()

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect {
            when (it) {
                SettingsEvent.PopBack -> navController.popBackStack()
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
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                        }
                    }
                )
            },
            content = { pv ->
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                // 将json文件内容导入到数据库
                val openSelectPhotoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = { uri ->
                        val contentResolver = context.contentResolver

                        uri?.let {
                            // 使用 ContentResolver 打开输入流
                            contentResolver.openInputStream(uri)?.use { inputStream ->
                                // 使用 Moshi 解析 JSON 数据
                                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                                val jsonAdapter = moshi.adapter<List<WatchEntity>>(Types.newParameterizedType(List::class.java, WatchEntity::class.java))

                                // 从输入流读取 JSON 内容
                                val jsonString = inputStream.bufferedReader().use { it.readText() }

                                // 解析 JSON 为 WatchEntity 对象列表
                                val watchEntities = jsonAdapter.fromJson(jsonString)

                                // 检查解析是否成功并将数据插入到数据库
                                if (watchEntities != null) {
                                    viewModel.uos(*watchEntities.map { it }.toTypedArray())

                                    Toast.makeText(context, "导入成功!${watchEntities[0].title}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "解析 JSON 失败!", Toast.LENGTH_SHORT).show()
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
                        val pt = Types.newParameterizedType(List::class.java, WatchEntity::class.java)

                        val jsonAdapter = moshi.adapter<List<WatchEntity>>(pt)
                        val toJson = jsonAdapter.toJson(settingsState.data)
                        outputStream?.use { stream ->
                            stream.write(toJson.toString().toByteArray())
                            stream.flush()
                        }
                    }
                )

//                val sortType by viewModel.sortType.collectAsState()
                val em = listOf(
                    "按照标题排序",
                    "按照创建时间排序",
                    "按照修改时间排序"
                )

                Column(modifier = Modifier.padding(top = pv.calculateTopPadding())) {
                    Column(
                        modifier = Modifier
                            .width(400.dp)
                    ) {
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
                        SettingItem(title = "清空数据", summary = "清空所有数据", onClick = {
                            scope.launch {
                                viewModel.dispatch(SettingsAction.ShowDialog)
                            }
                        })

                        /*Test(
                            title = "列表排序方式",
                            itemList = em,
                            summary = when (sortType) {
                                SortType.TITLE -> em[0]
                                SortType.CREATE_TIME -> em[1]
                                SortType.CHANGE_TIME -> em[2]
                            },
                            onClick = {
                                when (it) {
                                    0 -> {
//                                        viewModel.sortType(SortType.TITLE)
                                    }

                                    1 -> {
//                                        viewModel.sortType(SortType.CREATE_TIME)
                                    }

                                    else -> {
//                                        viewModel.sortType(SortType.CHANGE_TIME)
                                    }
                                }
                            }
                        )*/
                    }
                }

                if (settingsState.dialogStatus) {
                    AlertDialog(
                        title = {
                            Text(text = "是否要清除所有数据？")
                        },
                        onDismissRequest = {
                            scope.launch {
                                viewModel.dispatch(SettingsAction.DismissDialog)
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                scope.launch {
                                    viewModel.dispatch(SettingsAction.DismissDialog)
                                }
                            }) {
                                Text(text = "CANCEL")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                scope.launch {
                                    viewModel.dispatch(SettingsAction.ClearData)
                                }
                                Toast.makeText(
                                    context,
                                    "delete success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Text(text = "DELETE")
                            }
                        }
                    )
                }
            }
        )
    }
}