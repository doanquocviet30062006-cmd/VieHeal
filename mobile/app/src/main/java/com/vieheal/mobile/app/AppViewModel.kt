package com.vieheal.mobile.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vieheal.mobile.core.security.SessionController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    private val sessionController: SessionController,
) : ViewModel() {
    val startupState: StateFlow<AppStartupState> =
        sessionController.state
            .map(::resolveStartupState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, AppStartupState.Initializing)

    init {
        viewModelScope.launch { sessionController.restore() }
    }

    companion object {
        fun factory(sessionController: SessionController): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AppViewModel(sessionController) }
            }
    }
}
