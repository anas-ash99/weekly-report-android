package com.anas.weeklyreport.presentaion.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.extension_methods.setLocale
import com.anas.weeklyreport.extension_methods.shimmerEffect
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.presentaion.home.report_list.ReportList
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.ReportListType
import com.anas.weeklyreport.viewmodels.HomeViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("FrequentlyChangedStateReadInComposition", "UnrememberedMutableState")
@Composable
fun HomeScreen(navController: NavController?) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val viewmodel:HomeViewmodel = hiltViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val state = viewmodel.state.collectAsState()
    viewmodel.init(Locale.getDefault().language)

    LaunchedEffect(state.value.appLanguage){
        context.setLocale(state.value.appLanguage)
        viewmodel.forceRecomposition()
    }
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isOpen }
            .collect { isOpen ->
                viewmodel.onEvent(HomeScreenEvent.RequestNavigationDrawer(isOpen))
            }
    }

    LaunchedEffect(state.value.isNavigationDrawerOpen){

        scope.launch {
            if (state.value.isNavigationDrawerOpen){
                drawerState.open()
            }else{
                drawerState.close()
            }
        }
    }

    Surface {

        ModalNavigationDrawer(
            drawerState = drawerState ,
            drawerContent = {
                NavigationDrawerContent(viewmodel::onEvent)
            }
        ) {

            Scaffold(
                floatingActionButton = {
                    if (!state.value.itemsLoading){
                        FloatingActionButton(
                            onClick = { viewmodel.onEvent(HomeScreenEvent.OnCreateNewDocumentClick) },
                            containerColor = AppColors.APP_MAIN_COLOR.color,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 10.dp)
                                    .animateContentSize()
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = stringResource(id = R.string.new_report),
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                                if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                                    Text(
                                        stringResource(id = R.string.new_report),
                                        fontSize = 17.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            ) { innerPadding ->

                Text(text ="${state.value.recomposeTrigger}", fontSize = 0.sp)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    MyAppBar(
                        isLoading  = state.value.isAppLanguageLoading,
                        onMenuClick = {
                        viewmodel.onEvent(HomeScreenEvent.RequestNavigationDrawer(true))
                    })
                    Divider(color = AppColors.DIVIDER.color, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {  viewmodel.onEvent(HomeScreenEvent.RequestNotificationMessage(true)) }
                            .height(60.dp), // Add some top padding for the Row
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.value.isAppLanguageLoading){
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(start = 16.dp)
                                    .height(25.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect(),
                            )
                        }else{
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                ,
                                text = stringResource(id = R.string.all_reports),
                                fontSize = 18.sp
                            )
                        }
                    }
                    ReportList(state = state.value,
                        onEvent = viewmodel::onEvent,
                        reports = viewmodel.reports.toMutableList(),
                        type = ReportListType.ALL.toString(),
                        navController = navController!!,
                    )
                }
            }
        }

        MyNotificationMessage(message = state.value.toastMessage, isVisible = state.value.isNotificationMessageShown){
            viewmodel.onEvent(HomeScreenEvent.RequestNotificationMessage(false))
        }
        LoadingSpinner(isVisible = state.value.screenLoading)

    }

}

@Preview
@Composable
fun HomeScreenPreview() {
       HomeScreen(null)
}