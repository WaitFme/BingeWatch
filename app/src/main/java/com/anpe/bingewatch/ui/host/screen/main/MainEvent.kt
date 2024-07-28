package com.anpe.bingewatch.ui.host.screen.main

sealed class MainEvent {
    data object OpenBottomSheet: MainEvent()
    data object CloseBottomSheet: MainEvent()
}