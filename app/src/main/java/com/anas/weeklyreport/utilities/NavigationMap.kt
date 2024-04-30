package com.anas.weeklyreport.utilities

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anas.weeklyreport.presentaion.settings.SettingsScreen
import com.anas.weeklyreport.presentaion.account.UserDetailsScreen
import com.anas.weeklyreport.presentaion.feedback.FeedbackScreen
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.presentaion.report_creator.ReportCreatorScreen
import com.anas.weeklyreport.presentaion.home.HomeScreen
import com.anas.weeklyreport.presentaion.home.report_list.ReportListScreen
import kotlin.math.roundToInt


@Composable
fun NavigationMap(isUserLoggedIn:Boolean){
    val navController = rememberNavController()
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthDp = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    val screenHeightPx = with(density) { screenHeightDp.toPx().roundToInt() }
    val screenWidthPx = with(density) { screenWidthDp.toPx().roundToInt() }
    NavHost(
        navController = navController,
        //startDestination = AppScreen.HOME_SCREEN.toString()
        startDestination = if(isUserLoggedIn) AppScreen.HOME_SCREEN.toString() else AppScreen.USER_DETAILS.toString()
    ){
        composable(
            route = AppScreen.HOME_SCREEN.toString(),

            exitTransition = {

                    slideOutHorizontally(
                        targetOffsetX = {-screenWidthPx },
                        animationSpec = tween(500)
                    )

            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = {  -screenWidthPx }, animationSpec = tween(500))
            }
        ) {
            HomeScreen(navController)
        }
        composable(
            route = "${AppScreen.REPORT_LIST_SCREEN}/{type}",

            enterTransition = {
                slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            exitTransition = {
//                slideOutVertically(targetOffsetY = { -screenHeightPx }, animationSpec = tween(500))
                slideOutHorizontally(targetOffsetX = {-screenWidthPx }, animationSpec = tween(500))
            },
            popEnterTransition = {
//                slideInVertically(initialOffsetY = { -screenHeightPx }, animationSpec = tween(500))
                slideInHorizontally(initialOffsetX = {  -screenWidthPx }, animationSpec = tween(500))
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
        composable(
            route = AppScreen.CREATE_REPORT_SCREEN.toString() + "/{reportId}",
//            enterTransition = {
//
//                slideInVertically(initialOffsetY = { screenHeightPx }, animationSpec = tween(500))
//            }, popExitTransition = {
//                slideOutVertically(targetOffsetY = { screenHeightPx }, animationSpec = tween(500))
//            },
            enterTransition = {
                slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(500))
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
            route = AppScreen.USER_DETAILS.toString(),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(500))
            }
        ) {
            UserDetailsScreen(navController)
        }

        composable(
            route = AppScreen.SETTINGS.toString(),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(500))
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = {-screenWidthPx }, animationSpec = tween(500))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = {  -screenWidthPx }, animationSpec = tween(500))
            }
        ){
            SettingsScreen(navController = navController)
        }

        composable(
            route = AppScreen.FEEDBACK.toString(),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(500))
            }
        ) {
            FeedbackScreen(navController)
        }
    }
}


// slid in from left to right
//enterTransition = {
//    slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300))
//}, popExitTransition = {
//    slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300))
//},