package com.example.getevent.ui.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getevent.data.remote.StatisticsResponse
import com.example.getevent.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    var stats by mutableStateOf<StatisticsResponse?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            isLoading = true
            error = null
            val result = repository.getStatistics()
            isLoading = false
            result.onSuccess {
                stats = it
            }.onFailure { e ->
                error = e.message ?: "Failed to load statistics"
            }
        }
    }
}
