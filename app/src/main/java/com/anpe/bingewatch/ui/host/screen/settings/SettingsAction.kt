package com.anpe.bingewatch.ui.host.screen.settings

sealed class SettingsAction {
    data object ExportData: SettingsAction()
    data object ImportData: SettingsAction()
    data object ClearData: SettingsAction()

    data class ChangeServerAddress(val url: String): SettingsAction()
    data class ChangeSortType(val sortType: Int): SettingsAction()
}