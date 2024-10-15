package com.anas.weeklyreport.presentaion.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors

@Composable
fun NavigationDrawerContent(
    onEvent: (HomeScreenEvent) -> Unit,
    state:HomeScreenState
) {
    val context = LocalContext.current
    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onEvent(HomeScreenEvent.OnGoogleSignInResult(it))
        })

    val items = listOf(
        NavigationDrawerItem(
            painter = painterResource(id = R.drawable.outline_bookmark_border_24),
            name = R.string.bookmarks
        ),
        NavigationDrawerItem(
            imageVector = Icons.Outlined.Delete,
            name = R.string.trash
        ),
        NavigationDrawerItem(
            imageVector = Icons.Outlined.Settings,
            name = R.string.settings
        ),
        NavigationDrawerItem(
            imageVector = Icons.Default.ExitToApp,
            name = R.string.sign_out,
            visible = if (loggedInUser != null) loggedInUser?.id?.isNotBlank()!! else false
        )
    )
    ModalDrawerSheet (
        drawerContainerColor = Color(0xFFF5F5F5)
    ){

        Column(
            Modifier
                .fillMaxSize()

        ){
           if (loggedInUser != null && loggedInUser?.id?.isNotBlank()!!){
               DrawerHeader(onEvent = onEvent, user = loggedInUser!!)
           }else{
               DrawerHeaderGoogle{
                   val googleSignInClient = initGoogleSignInClient(context)
                   val signInIntent = googleSignInClient.signInIntent
                   startForResult.launch(signInIntent)
               }
           }
            Divider()
            items.forEach { item ->

                if (item.visible){
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                        label = {
                            Text(
                                text = stringResource(id = item.name),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = if (item.name == R.string.sign_out) AppColors.NotificationErrorColor else Color.Unspecified)
                                },
                        selected = false,
                        icon = {
                            Icon(
                                painter = item.painter ?: rememberVectorPainter(image = item.imageVector!!) ,
                                contentDescription = stringResource(id = item.name),
                                tint = if (item.name == R.string.sign_out) AppColors.NotificationErrorColor else Color.Unspecified
                            )

                        },
                        onClick = {
                            when(item.name){
                                R.string.profile -> onEvent(HomeScreenEvent.OnEditProfileClick)
                                R.string.language -> onEvent(HomeScreenEvent.OnLanguageDrawerClick)
                                R.string.bookmarks -> onEvent(HomeScreenEvent.OnBookmarkDrawerClick)
                                R.string.trash -> onEvent(HomeScreenEvent.OnTrashDrawerClick)
                                R.string.settings -> onEvent(HomeScreenEvent.OnSettingsClick)
                                R.string.sign_out -> onEvent(HomeScreenEvent.OnSignOutClick(context))
                            }
                        }
                    )
                }

            }
        }
    }

}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    user: User,
    onEvent: (HomeScreenEvent) -> Unit,
){
    Spacer(modifier = Modifier.height(10.dp))
//    Image(
//        painter = painterResource(id = R.drawable.user_icon),
//        contentDescription = stringResource(id = R.string.user_profile),
//
//        Modifier
//            .size(50.dp)
//            .padding(start = 16.dp)
//    )
    MyProfilePic(modifier = Modifier
        .padding(start = 16.dp)
        .size(50.dp))
    Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = { onEvent(HomeScreenEvent.OnEditProfileClick) },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    user.fullName,
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
                Text(
                    user.email,
                    modifier = Modifier.padding(start = 16.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                modifier = Modifier.padding(end = 10.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.more_options)
            )
        }
    }

}
@Composable
fun DrawerHeaderGoogle(
    modifier: Modifier = Modifier,
    onClick:()->Unit
){
    TextButton(
        shape = RoundedCornerShape(0.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(contentColor = Color.Unspecified, containerColor = Color.Transparent)
    ) {
        Row (
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 5.dp),
            horizontalArrangement= Arrangement.SpaceBetween,
            verticalAlignment= Alignment.CenterVertically,
        ){
            Image(
                painter = painterResource(id = R.drawable.icons_google),
                contentDescription = stringResource(id = R.string.user_profile),
                Modifier
                    .size(32.dp)
                    .padding(start = 5.dp)
            )

            Column (
                verticalArrangement= Arrangement.Center,
                horizontalAlignment= Alignment.Start,
            ){
                Text(
                    text = stringResource(id = R.string.backup_and_restore),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(id = R.string.sign_and_synchronize),
                    color = Color(0x94000000)
                )
            }
            Icon(painterResource(id = R.drawable.baseline_sync_24), contentDescription = stringResource(id = R.string.more_options) )
        }
    }


}


@Composable
fun MyProfilePic(
    modifier:Modifier = Modifier,
    iconSize:Dp = 30.dp
) {
    Box(
        modifier = modifier
            .size(55.dp)
            .aspectRatio(1f) // Ensure it's a circle
            .background(Color.LightGray, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Outlined.Person, contentDescription = "", Modifier.size(iconSize), tint = Color(
            0xCE161616
        )
        )
    }
}

@Preview
@Composable
fun NavigationSheetReview() {
    NavigationDrawerContent(onEvent = {}, HomeScreenState(isUserSignedIn = true))
}
data class NavigationDrawerItem(
    val imageVector:ImageVector? = null,
    val painter:Painter? = null,
    val name:Int,
    val visible:Boolean = true,
)