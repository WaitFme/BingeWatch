package com.anpe.bingewatch.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anpe.bingewatch.ui.host.screen.splash.AnimatedSplashScreen
import com.anpe.bingewatch.ui.host.screen.home.HomeScreen
import com.anpe.bingewatch.ui.host.screen.settings.SettingsScreen
import com.anpe.bingewatch.ui.host.manage.ScreenManager
import com.anpe.bingewatch.ui.host.screen.edit.EditScreen
import com.anpe.bingewatch.ui.theme.BingeWatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        enableEdgeToEdge()
        setContent {
            BingeWatchTheme {
                val navControllerScreen = rememberNavController()

                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navControllerScreen,
                    startDestination = ScreenManager.MainScreen.route,
                ) {
                    composable(route = ScreenManager.SplashScreen.route) {
                        AnimatedSplashScreen(
                            navControllerScreen = navControllerScreen,
                        )
                    }
                    composable(route = ScreenManager.MainScreen.route) {
                        HomeScreen(
                            navControllerScreen = navControllerScreen,
                        )
                    }
                    composable(route = ScreenManager.SettingsScreen.route) {
                        SettingsScreen(
                            navController = navControllerScreen
                        )
                    }
                    composable(route = ScreenManager.EditScreen.route) {
                        EditScreen(
                            navController = navControllerScreen
                        )
                    }
                }
            }
        }
    }
}