package com.anpe.bingewatch.ui.host.manage

import androidx.annotation.StringRes
import com.anpe.bingewatch.R

sealed class ScreenManager(val route: String, @StringRes val resourceId: Int) {
    object SplashScreen: ScreenManager("splash_screen", R.string.splash_screen)
    object MainScreen: ScreenManager("first_screen", R.string.main_screen)
    object SettingsScreen: ScreenManager("Settings", R.string.settings_screen)
}