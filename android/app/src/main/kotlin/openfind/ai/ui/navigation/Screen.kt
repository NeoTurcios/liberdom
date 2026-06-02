package openfind.ai.ui.navigation

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object BulkScan : Screen("bulk")
    data object Generator : Screen("generator")
    data object Library : Screen("library")
    data object Watchlist : Screen("watchlist")
    data object Settings : Screen("settings")

    companion object {
        val bottomNavScreens = listOf(Search, BulkScan, Generator, Library, Watchlist)
    }
}
