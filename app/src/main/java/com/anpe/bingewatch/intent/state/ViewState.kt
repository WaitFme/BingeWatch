package com.anpe.bingewatch.intent.state

data class ViewState(
    val visible: Boolean,
    val onShowRequest: () -> Unit,
    val onDismissRequest: () -> Unit
)