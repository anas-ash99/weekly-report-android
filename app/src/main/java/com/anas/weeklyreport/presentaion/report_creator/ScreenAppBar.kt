package com.anas.weeklyreport.presentaion.report_creator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.domain.ReportCreatorScreenEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAppBar(navController:NavController?, onEvent:(ReportCreatorScreenEvent) ->Unit ){

    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.create_report), modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp), textAlign = TextAlign.Center)
        },
        navigationIcon = {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
            }
        },
        actions = {
            IconButton(onClick = { onEvent(ReportCreatorScreenEvent.OnAiDialogRequest(true)) }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ai_star),
                    contentDescription = stringResource(id = R.string.user_profile)
                )
            }
        },
    )


}

@Preview
@Composable
fun ScreenAppBarReview() {
    ScreenAppBar(navController = null,{} )
}