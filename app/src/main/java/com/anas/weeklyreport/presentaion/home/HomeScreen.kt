package com.anas.weeklyreport.presentaion.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.R
import com.anas.weeklyreport.extension_methods.setLocale
import com.anas.weeklyreport.extension_methods.shimmerEffect
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.presentaion.home.bottom_sheet.HomeBottomSheet
import com.anas.weeklyreport.presentaion.home.report_list.ReportList
import com.anas.weeklyreport.presentaion.home.report_list.openWordDocument
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.shared.ReportListType
import com.anas.weeklyreport.viewmodels.HomeViewmodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.system.exitProcess


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FrequentlyChangedStateReadInComposition", "UnrememberedMutableState")
@Composable
fun HomeScreen(navController: NavController?) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val viewmodel:HomeViewmodel = hiltViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val state = viewmodel.state.collectAsState()
    val reports = viewmodel.reports.collectAsState()
    val snackbarHostState = remember {SnackbarHostState()}
    val sheetState = rememberModalBottomSheetState()


    LaunchedEffect(state.value.screen){ // launchedEffect for handling navigation
        if (state.value.screen == AppScreen.HOME_SCREEN.toString()){
            navController?.popBackStack()
        }else if (state.value.screen.isNotBlank()){
            navController?.navigate(state.value.screen){
              if (loggedInUser ==  null){
                  popUpTo(AppScreen.HOME_SCREEN.toString()) { // remove the current screen from the back stack
                      inclusive = true
                  }
              }
            }
            state.value.screen = ""
        }
    }
    LaunchedEffect(state.value.documentUri){ // open the downloaded document
        state.value.documentUri?.let {
            openWordDocument(context, it)
            state.value.documentUri = null
        }
    }



    LaunchedEffect(Unit){
        viewmodel.init(Locale.getDefault().language)
    }
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
                NavigationDrawerContent(viewmodel::onEvent, state.value)
            }
        ) {
            Scaffold(
                snackbarHost  = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                floatingActionButton = {
                    if (!state.value.itemsLoading){
                        FloatingActionButton(
                            onClick = { viewmodel.onEvent(HomeScreenEvent.OnCreateNewDocumentClick) },
                            containerColor = AppColors.APP_MAIN_COLOR,
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
                        .background(Color.White)
                        .padding(innerPadding)
                ) {
                    MyAppBar(
                        isLoading  = state.value.isAppLanguageLoading,
                        onMenuClick = {
                        viewmodel.onEvent(HomeScreenEvent.RequestNavigationDrawer(true))
                    })
                    Divider(color = AppColors.DIVIDER, thickness = 1.dp)


                    if (reports.value.none { !it.isInTrash } && !state.value.itemsLoading){
                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){

                            Image(
                                painter = painterResource(id = R.drawable.empty_screen_no_background),
                                contentDescription ="" ,
                                modifier= Modifier.fillMaxWidth().padding(bottom = 20.dp),
                                contentScale = ContentScale.FillWidth
                            )
                            Text(
                                text = "No Reports",
                                fontSize = 20.sp,
                                modifier= Modifier.padding(bottom = 20.dp)
                            )
                        }
                    }else{
                        if (state.value.isAppLanguageLoading){
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(horizontal = 16.dp, vertical = 15.dp)
                                    .height(25.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .shimmerEffect(),
                            )
                        }else{
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 15.dp)
                                ,
                                text = stringResource(id = R.string.all_reports),
                                fontSize = 18.sp
                            )
                        }

                        ReportList(state = state.value,
                            onEvent = viewmodel::onEvent,
                            reports = reports.value,
                            type = ReportListType.ALL.toString(),
                        )
                    }
                }
            }
        }

        MyNotificationMessage(
            message = if (state.value.notificationMessage != 0 ) stringResource(id = state.value.notificationMessage) else "",
            color = state.value.notificationColor,
            isVisible = state.value.isNotificationMessageShown){
            viewmodel.onEvent(HomeScreenEvent.RequestNotificationMessage(false))
        }
        LoadingSpinner(isVisible = state.value.screenLoading)
        HomeBottomSheet(state = state.value, sheetState = sheetState,  onEvent = viewmodel::onEvent)
    }

}

fun initGoogleSignInClient(context: Context) : GoogleSignInClient {
     val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
  return GoogleSignIn.getClient(context, gso)
}

@Preview(device = "id:pixel_6_pro")
@Composable
fun HomeScreenPreview() {

}