package com.vieheal.mobile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AppStartupTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun unauthenticatedStartupShowsAuthAndDoesNotNavigateHome() {
        composeRule.onNodeWithText("Sign in required").assertIsDisplayed()
        composeRule.onNodeWithText("Authenticated workspace").assertDoesNotExist()
    }
}
