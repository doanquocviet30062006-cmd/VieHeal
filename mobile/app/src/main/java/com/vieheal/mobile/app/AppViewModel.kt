package com.vieheal.mobile.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vieheal.mobile.core.security.SessionStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    private val sessionStateProvider: SessionStateProvider,
) : ViewModel() {
    private val _startupState = MutableStateFlow<AppStartupState>(AppStartupState.Initializing)
    val startupState: StateFlow<AppStartupState> = _startupState.asStateFlow()

    init {
        refreshStartupState()
    }

    fun refreshStartupState() {
        _startupState.value = resolveStartupState(sessionStateProvider.currentState())
    }

    companion object {
        fun factory(sessionStateProvider: SessionStateProvider): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AppViewModel(sessionStateProvider) }
            }
    }
}
