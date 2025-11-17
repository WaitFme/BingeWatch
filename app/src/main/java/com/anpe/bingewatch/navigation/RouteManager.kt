package com.anpe.bingewatch.navigation

import androidx.annotation.StringRes
import com.anpe.bingewatch.R

sealed class RouteManager(val route: String, @StringRes val resourceId: Int) {
    object SplashScreen: RouteManager("splash_screen", R.string.splash_screen)
    object MainScreen: RouteManager("first_screen", R.string.main_screen)
    object SettingsScreen: RouteManager("Settings", R.string.settings_screen)
    object EditScreen: RouteManager("Edit", R.string.settings_screen)
}