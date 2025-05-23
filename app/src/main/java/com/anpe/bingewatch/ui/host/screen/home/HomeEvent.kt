package com.anpe.bingewatch.ui.host.screen.home

sealed class HomeEvent {
    data object PopBack: HomeEvent()
    data object ShowDialog: HomeEvent()
    data object CloseDialog: HomeEvent()
    data class NaviScreen(val route: String): HomeEvent()
    data class ShowToast(val text: String): HomeEvent()
}