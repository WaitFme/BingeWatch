package com.anpe.bingewatch.ui.host.screen.settings

sealed class SettingsAction {
    data object ExportData: SettingsAction()
    data object ImportData: SettingsAction()
    data object ClearData: SettingsAction()
    data object ShowDialog: SettingsAction()
    data object DismissDialog: SettingsAction()
    data object Sync: SettingsAction()
    data object Upload: SettingsAction()
    data class SettingServerAddress(val url: String): SettingsAction()
}