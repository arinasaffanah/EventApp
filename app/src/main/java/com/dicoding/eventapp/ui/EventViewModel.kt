package com.dicoding.eventapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.eventapp.data.repository.EventRepository
import com.dicoding.eventapp.data.response.ListEventsItem
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventDetails = MutableLiveData<ListEventsItem?>()
    val eventDetails: LiveData<ListEventsItem?> get() = _eventDetails

    fun getEventDetails(eventId: String) {
        viewModelScope.launch {
            try {
                val event = repository.getEventById(eventId) // Memanggil metode di repository
                _eventDetails.value = event // Update LiveData dengan detail acara
            } catch (e: Exception) {
                _eventDetails.value = null // Tangani kesalahan jika ada
            }
        }
    }
}