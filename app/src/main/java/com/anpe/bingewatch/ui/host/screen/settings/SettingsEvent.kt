package com.anpe.bingewatch.ui.host.screen.settings

sealed class SettingsEvent {
    data object PopBack: SettingsEvent()
}