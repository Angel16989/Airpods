package com.angel16989.appleicon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.angel16989.appleicon.ui.AppleIconApp
import com.angel16989.appleicon.ui.theme.AppleIconTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppleIconTheme {
                AppleIconApp()
            }
        }
    }
}
