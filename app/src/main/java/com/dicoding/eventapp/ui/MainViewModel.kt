package com.dicoding.eventapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.eventapp.data.repository.EventRepository
import com.dicoding.eventapp.data.response.EventResponse
import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.data.retrofit.ApiConfig
import com.dicoding.eventapp.utils.Events
import kotlinx.coroutines.launch


class MainViewModel (private val repository: EventRepository) : ViewModel() {
    private val _eventList = MutableLiveData<EventResponse>()
    val eventList: LiveData<EventResponse?> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _snackBar = MutableLiveData<Events<String>>()
    val snackBar: LiveData<Events<String>> get() = _snackBar

    private val _eventDetails = MutableLiveData<ListEventsItem?>() // Ubah tipe menjadi ListEventsItem
    val eventDetails: LiveData<ListEventsItem?> get() = _eventDetails


    companion object {
        private const val TAG = "MainViewModel"
    }

    fun getActiveEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getActiveEvents() // Memanggil API service untuk mendapatkan acara aktif
                _eventList.value = response // Mengupdate LiveData dengan daftar acara
            } catch (e: Exception) {
                _snackBar.value = Events("Terjadi kesalahan: ${e.message}")
                Log.e(TAG, "onFailure: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPastEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService().getPastEvents() // Panggil metode API
                _eventList.value = response // Atur hasil ke LiveData
            } catch (e: Exception) {
                // Tangani kesalahan
                _snackBar.value = Events("Terjadi kesalahan saat mengambil acara.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEventDetails(eventId: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEventDetails(eventId)
                val eventDetail = response.listEvents.find { it.id == eventId.toInt() } // Mengambil detail acara dari listEvents
                _eventDetails.value = eventDetail // Update LiveData dengan detail acara
            } catch (e: Exception) {
                _snackBar.value = Events("Terjadi kesalahan saat mengambil detail acara.")
            }
        }
    }
}