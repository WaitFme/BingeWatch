package com.anpe.bingewatch.ui.host.screen.home

import androidx.compose.ui.text.input.TextFieldValue


sealed class HomeAction {
    data class NaviScreen(val route: String): HomeAction()
    data class ChangeTabIndex(val index: Int): HomeAction()
    data class ChangeCurrentEpi(val cEpi: TextFieldValue): HomeAction()
    data class ChangeTotalEpi(val tEpi: TextFieldValue): HomeAction()
    data class IncreaseEpi(val id: Long): HomeAction()
    data class DecreaseEpi(val id: Long): HomeAction()
    data class DeleteData(val id: Long): HomeAction()
    data class UpdateData(val id: Long): HomeAction()
    data class ShowDialog(val id: Long): HomeAction()

    data object DismissDialog: HomeAction()
    data object RefreshData : HomeAction()
}
