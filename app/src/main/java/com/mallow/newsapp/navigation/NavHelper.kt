package com.mallow.newsapp.navigation


import androidx.navigation.NavHostController

fun NavHostController.navigateTo(screen: String) {
    navigate(screen)
}

fun NavHostController.navigateNew(screen: String) {
    navigate(screen) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

