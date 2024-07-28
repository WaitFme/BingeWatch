package com.anpe.bingewatch.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anpe.bingewatch.ui.host.screen.splash.AnimatedSplashScreen
import com.anpe.bingewatch.ui.host.screen.main.MainScreen
import com.anpe.bingewatch.ui.host.screen.settings.SettingsScreen
import com.anpe.bingewatch.ui.host.manage.ScreenManager
import com.anpe.bingewatch.ui.theme.BingeWatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        enableEdgeToEdge()
        setContent {
            val navControllerScreen = rememberNavController()

            BingeWatchTheme {
                Surface(
                    Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f)
                ) {
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
                            MainScreen(
                                navControllerScreen = navControllerScreen,
                            )
                        }
                        composable(route = ScreenManager.SettingsScreen.route) {
                            SettingsScreen(
                                navController = navControllerScreen
                            )
                        }
                    }
                }
            }
        }
    }
}