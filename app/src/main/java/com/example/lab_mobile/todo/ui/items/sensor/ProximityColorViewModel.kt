package com.example.myapp3

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch

class ProximityColorViewModel(application: Application) : AndroidViewModel(application) {
    // 0.0 = very close (blue), 1.0 = far away (white)
    var proximityDistance by mutableFloatStateOf(1f)
        private set

    init {
        viewModelScope.launch {
            ProximitySensorMonitor(getApplication()).distance.collect {
                proximityDistance = it
            }
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProximityColorViewModel(application)
            }
        }
    }
}

