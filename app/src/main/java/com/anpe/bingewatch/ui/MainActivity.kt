package com.anpe.bingewatch.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anpe.bingewatch.navigation.RouteManager
import com.anpe.bingewatch.ui.feature.home.HomeScreen
import com.anpe.bingewatch.ui.host.screen.edit.EditScreen
import com.anpe.bingewatch.ui.host.screen.settings.SettingsScreen
import com.anpe.bingewatch.ui.feature.splash.AnimatedSplashScreen
import com.anpe.bingewatch.ui.theme.BingeWatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
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
                    startDestination = RouteManager.MainScreen.route,
                ) {
                    composable(route = RouteManager.SplashScreen.route) {
                        AnimatedSplashScreen(
                            navControllerScreen = navControllerScreen,
                        )
                    }
                    composable(route = RouteManager.MainScreen.route) {
                        HomeScreen(
                            navControllerScreen = navControllerScreen,
                        )
                    }
                    composable(route = RouteManager.SettingsScreen.route) {
                        SettingsScreen(
                            navController = navControllerScreen
                        )
                    }
                    composable(route = RouteManager.EditScreen.route) {
                        EditScreen(
                            navController = navControllerScreen
                        )
                    }
                }
            }
        }
    }
}