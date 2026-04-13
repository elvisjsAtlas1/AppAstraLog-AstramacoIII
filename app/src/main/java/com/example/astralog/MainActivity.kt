package com.example.astralog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.astralog.ui.navigation.AppNavigation
import com.example.astralog.ui.theme.AstraLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstraLogTheme {
                AppNavigation()
            }
        }
    }
}