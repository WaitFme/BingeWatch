package com.anpe.bingewatch.ui.host.screen.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.anpe.bingewatch.ui.host.screen.main.MainViewModel
import com.anpe.bingewatch.utils.SortType
import com.anpe.bingewatch.ui.widget.SettingItem
import com.anpe.bingewatch.ui.widget.Test
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel: MainViewModel = hiltViewModel()
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
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                        }
                    }
                )
            },
            content = { pv ->
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                val openSelectPhotoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = {
                        /*it?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                imagePath.value = Tools.uriToFileApiQ(context, it)!!
                                sp.edit().putString("logoImagePath", imagePath.value).apply()
                            }
                        }*/
                    }
                )

                var dialogStatus by remember {
                    mutableStateOf(false)
                }

                val sortType by viewModel.sortType.collectAsState()
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
//                                val gson = Gson()
//                                val allWatch = viewModel.getAllWatch()
//                                val json = gson.toJson(allWatch)
                            }
                        })
                        SettingItem(title = "导入数据", summary = "从磁盘导入数据", onClick = {
                            openSelectPhotoLauncher.launch("file/*")
                        })
                        SettingItem(title = "清空数据", summary = "清空所有数据", onClick = {
                            dialogStatus = !dialogStatus
                        })

                        Test(
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
                        )
                    }
                }

                if (dialogStatus) {
                    AlertDialog(
                        title = {
                            Text(text = "是否要清除所有数据？")
                        },
                        onDismissRequest = { dialogStatus = false },
                        dismissButton = {
                            Button(onClick = { dialogStatus = false }) {
                                Text(text = "cancel")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
//                                viewModel.deleteAllWatch()
                                Toast.makeText(
                                    context,
                                    "delete success",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialogStatus = false
                            }) {
                                Text(text = "delete")
                            }
                        }
                    )
                }
            }
        )
    }
}