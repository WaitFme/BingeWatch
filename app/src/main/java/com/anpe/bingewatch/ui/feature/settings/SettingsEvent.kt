package com.anpe.bingewatch.ui.host.screen.settings

sealed class SettingsEvent {
    data object PopBack: SettingsEvent()
    data class Toast(val msg: String): SettingsEvent()
}