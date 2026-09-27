package com.vieheal.mobile.app

import com.vieheal.mobile.core.security.SessionStateProvider
import com.vieheal.mobile.core.security.UnconfiguredSessionStateProvider

interface AppContainer {
    val sessionStateProvider: SessionStateProvider
}

class DefaultAppContainer : AppContainer {
    override val sessionStateProvider: SessionStateProvider = UnconfiguredSessionStateProvider
}
