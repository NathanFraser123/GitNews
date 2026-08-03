package com.st10445368.gitbbc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.st10445368.gitbbc.navigation.MainAppContainer
import com.st10445368.gitbbc.ui.theme.GitBBCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitBBCTheme {
                MainAppContainer()
            }
        }
    }
}