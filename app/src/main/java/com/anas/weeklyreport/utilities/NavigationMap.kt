package com.anas.weeklyreport.utilities

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.presentaion.report_creator.ReportCreatorScreen
import com.anas.weeklyreport.presentaion.home.HomeScreen
import com.anas.weeklyreport.presentaion.home.report_list.ReportListScreen


@Composable
fun NavigationMap(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.HOME_SCREEN.toString()){

        composable(AppScreen.HOME_SCREEN.toString()) {
            HomeScreen(navController)
        }
        composable(
            route = AppScreen.CREATE_DOCUMENT_SCREEN.toString() + "/{reportId}",
            enterTransition = {
                slideInVertically(initialOffsetY = { 1000 }, animationSpec = tween(300))
            }, popExitTransition = {
                slideOutVertically(targetOffsetY = { 1000 }, animationSpec = tween(100))
            },
            arguments = listOf(
                navArgument("reportId"){
                    type = NavType.StringType
                    defaultValue = "empty"
                    nullable = true
                }
            )
        ){ entry ->
            ReportCreatorScreen(navController, reportId = entry.arguments?.getString("reportId")!!)
        }
        composable(
            route = "${AppScreen.REPORT_LIST_SCREEN}/{type}",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300))
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300))
            },
            arguments = listOf(
                navArgument("type"){
                    type = NavType.StringType
                    defaultValue = "empty"
                    nullable = true
                }
            )
        ){ entry ->
            ReportListScreen(navController, type = entry.arguments?.getString("type")!!)
        }
    }
}