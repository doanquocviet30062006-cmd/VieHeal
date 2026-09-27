package com.vieheal.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vieheal.mobile.app.DefaultAppContainer
import com.vieheal.mobile.app.VieHealApp

class MainActivity : ComponentActivity() {
    private val appContainer: DefaultAppContainer by lazy { DefaultAppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VieHealApp(container = appContainer)
        }
    }
}
