package com.vieheal.mobile

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            TextView(this).apply {
                text = "VieHeal"
                textSize = 28f
                gravity = Gravity.CENTER
                contentDescription = "VieHeal"
            }
        )
    }
}
