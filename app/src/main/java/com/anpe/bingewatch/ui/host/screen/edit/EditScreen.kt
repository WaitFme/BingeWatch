package com.anpe.bingewatch.ui.host.screen.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anpe.bingewatch.R
import com.anpe.bingewatch.utils.Tools.Companion.numberFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavHostController) {
    val viewModel: EditViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    val editState by viewModel.editState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                EditEvent.PopBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text("编辑")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch(Dispatchers.Default) {
                            viewModel.dispatch(EditAction.CreateData)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                    }
                }
            )
        },
        content = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding())
            ) {
                val (titleRef, ceRef, teRef, timeSelectRef) = createRefs()

                OutlinedTextField(
                    modifier = Modifier.constrainAs(titleRef) {
                        start.linkTo(parent.start, 15.dp)
                        top.linkTo(parent.top, 0.dp)
                        end.linkTo(parent.end, 15.dp)
                        width = Dimension.fillToConstraints
                    },
                    value = editState.title,
                    label = {
                        Text(
                            text = if (editState.titleAlive) stringResource(id = R.string.tv_name_tip) else
                                stringResource(id = R.string.tv_name)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {
                        viewModel.changeTitle(it)
                    }
                )

                OutlinedTextField(
                    modifier = Modifier.constrainAs(ceRef) {
                        start.linkTo(parent.start, 15.dp)
                        top.linkTo(titleRef.bottom)
                        end.linkTo(teRef.start, 5.dp)
                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 1f
                    },
                    value = editState.currentEpisode,
                    label = {
                        Text(text = stringResource(id = R.string.current_episode))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {
                        viewModel.changeCE(it)
                    }
                )

                OutlinedTextField(
                    modifier = Modifier.constrainAs(teRef) {
                        start.linkTo(ceRef.end, 5.dp)
                        top.linkTo(titleRef.bottom)
                        end.linkTo(parent.end, 15.dp)
                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 1f
                    },
                    value = editState.totalEpisode,
                    label = { Text(text = stringResource(id = R.string.total_episode)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {
                        viewModel.changeTE(it.numberFilter())
                    }
                )

                val tps = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis(),
                    initialDisplayMode = DisplayMode.Input
                )
                tps.selectedDateMillis?.let {
                    viewModel.changeCreateTime(it)
                }

                DatePicker(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .constrainAs(timeSelectRef) {
                            start.linkTo(parent.start, 15.dp)
                            top.linkTo(ceRef.bottom, 5.dp)
                            end.linkTo(parent.end, 15.dp)
                            width = Dimension.fillToConstraints
                        },
                    state = tps,
                )
            }
        }
    )
}