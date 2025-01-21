package com.mallow.newsapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mallow.newsapp.view.ArticlesListScreen
import com.mallow.newsapp.view.DetailScreen
import com.mallow.newsapp.view.PreferenceScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateNavigationGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screens.LIST) {

        composable(Screens.LIST) {
            ArticlesListScreen(navController)
        }

        composable(Screens.DETAIL, arguments = listOf(navArgument("id") { type = NavType.IntType }))
        { backStackEntry ->
            val articleId = backStackEntry.arguments?.getInt("id")
            DetailScreen(navController, articleId)
        }

        composable(Screens.PREFERENCE) {
            PreferenceScreen(navController)
        }
    }
}
