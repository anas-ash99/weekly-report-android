package com.anas.weeklyreport.presentaion


import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.data.repository.UserRepository
import com.anas.weeklyreport.utilities.NavigationMap
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()

        setContent {
            NavigationMap(userRepository.getUser() != null)
        }

    }


}
