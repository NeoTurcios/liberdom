package openfind.ai.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import openfind.ai.ui.components.BottomNavBar
import openfind.ai.ui.screens.BulkScreen
import openfind.ai.ui.screens.GeneratorScreen
import openfind.ai.ui.screens.LibraryScreen
import openfind.ai.ui.screens.SearchScreen
import openfind.ai.ui.screens.SettingsScreen
import openfind.ai.ui.screens.WatchlistScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Search.route

    val showBottomBar = Screen.bottomNavScreens.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Search.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ScreenTransition"
        ) { route ->
            NavHost(
                navController = navController,
                startDestination = Screen.Search.route
            ) {
                composable(Screen.Search.route) {
                    SearchScreen(
                        navController = navController,
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToAbout = {}
                    )
                }
                composable(Screen.BulkScan.route) {
                    BulkScreen(
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Generator.route) {
                    GeneratorScreen(
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onNavigateToSearch = { domain ->
                            navController.navigate(Screen.Search.route) {
                                popUpTo(Screen.Search.route) { inclusive = true }
                                launchSingleTop = true
                            }
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("prefill_domain", domain)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Watchlist.route) {
                    WatchlistScreen(
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
