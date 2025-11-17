package com.anpe.bingewatch.ui.host.screen.edit

sealed class EditAction {
    data object CreateData: EditAction()
}