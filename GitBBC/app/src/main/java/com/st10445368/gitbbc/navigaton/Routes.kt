package com.st10445368.gitbbc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Type-safe destinations
@Serializable object SearchRoute
@Serializable object SettingsRoute

// Map destinations to Bottom Bar UI items
sealed class BottomBarItem<T : Any>(
    val route: T,
    val title: String,
    val icon: ImageVector
) {
    data object Search : BottomBarItem<SearchRoute>(SearchRoute, "Search", Icons.Default.Search)
    data object Settings : BottomBarItem<SettingsRoute>(SettingsRoute, "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomBarItem.Search,
    BottomBarItem.Settings
)