package com.example.getevent.ui.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getevent.data.remote.dto.EventDto
import com.example.getevent.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    var events by mutableStateOf<List<EventDto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            isLoading = true
            error = null
            val result = repository.getAllEvents()
            isLoading = false
            result.onSuccess { response ->
                events = response.data ?: emptyList()
            }.onFailure { e ->
                error = e.message ?: "Failed to load events"
            }
        }
    }
}
