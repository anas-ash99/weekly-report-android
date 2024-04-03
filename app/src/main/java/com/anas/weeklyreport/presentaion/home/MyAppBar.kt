package com.anas.weeklyreport.presentaion.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(title: String, onMenuClick:() -> Unit) {

    TopAppBar(
        modifier = Modifier.padding(end = 10.dp),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu")
            }

        },
        actions = {
            Image(
                modifier = Modifier.size(35.dp).clip(CircleShape),
                painter = painterResource(id = R.drawable.weekly_report_icon1),
                contentDescription = "App Icon"
            )

        },

    )
}

@Preview
@Composable
fun MyAppBarPreview() {
    MyAppBar(title = "My App", {} ) // Replace with your image resource
}