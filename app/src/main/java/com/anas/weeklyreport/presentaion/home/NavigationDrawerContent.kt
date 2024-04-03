package com.anas.weeklyreport.presentaion.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.HomeScreenEvent

@Composable
fun NavigationDrawerContent(onEvent: (HomeScreenEvent) -> Unit) {


    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(10.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement= Arrangement.SpaceBetween,
            verticalAlignment= Alignment.CenterVertically,
        ){
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = stringResource(id = R.string.user_profile),
                Modifier
                    .size(60.dp)
                    .padding(start = 16.dp)
            )

           IconButton(modifier = Modifier.padding(end = 10.dp), onClick = { /*TODO*/ }) {
               Icon(imageVector = Icons.Outlined.Edit, contentDescription = stringResource(id = R.string.more_options) )
           }
        }
        Text("Anas Ashraf", modifier = Modifier.padding(start = 16.dp, top = 8.dp, ), style = MaterialTheme.typography.headlineSmall)
        Text("username@gmail.com", modifier = Modifier.padding(start = 16.dp, bottom = 10.dp), style = MaterialTheme.typography.bodyMedium)
        Divider()
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.language), fontWeight = FontWeight.Medium, fontSize = 16.sp) },
            selected = false,
            icon = {
                Icon(painter = painterResource(id = R.drawable.baseline_language_24)  , contentDescription = stringResource(id = R.string.language))
            },
            onClick = {
                onEvent(HomeScreenEvent.OnLanguageDrawerClick)
            }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.bookmarks),fontWeight = FontWeight.Medium, fontSize = 16.sp)  },
            selected = false,
            icon = {
                Icon(painter = painterResource(id = R.drawable.outline_bookmark_border_24) , contentDescription = stringResource(id = R.string.bookmarks))
            },
            onClick = {
                onEvent(HomeScreenEvent.OnBookmarkDrawerClick)
            }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(id = R.string.trash), fontWeight = FontWeight.Medium, fontSize = 16.sp)  },
            selected = false,
            icon = {
                Icon(imageVector = Icons.Outlined.Delete , contentDescription = stringResource(id = R.string.trash))
            },
            onClick = {
                onEvent(HomeScreenEvent.RequestNavigationDrawer(false))
                onEvent(HomeScreenEvent.OnTrashDrawerClick)
            }
        )


    }

}