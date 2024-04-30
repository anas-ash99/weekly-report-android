package com.anas.weeklyreport.presentaion.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.extension_methods.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(isLoading: Boolean, onMenuClick:() -> Unit) {

    TopAppBar(
        modifier = Modifier.padding(end = 10.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        title = {
            if (isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect(),
                )
            }else{
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.app_name),
                    textAlign = TextAlign.Center
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu")
            }

        },
        actions = {
            Image(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.weekly_report_icon1),
                contentDescription = "App Icon"
            )

        },

    )
}

@Preview
@Composable
fun MyAppBarPreview() {
    MyAppBar(isLoading = true, {} ) // Replace with your image resource
}