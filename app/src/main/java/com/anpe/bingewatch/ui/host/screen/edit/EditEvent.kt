package com.anpe.bingewatch.ui.host.screen.edit

sealed class EditEvent {
    data object PopBack: EditEvent()
}