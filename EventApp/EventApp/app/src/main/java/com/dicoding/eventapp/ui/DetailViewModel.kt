package com.dicoding.eventapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.eventapp.data.response.DetailEventResponse
import com.dicoding.eventapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val _eventDetail = MutableLiveData<DetailEventResponse>()
    val eventDetail: LiveData<DetailEventResponse> get() = _eventDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getEventDetail(eventId: String) {
        _isLoading.value = true // Mulai loading
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDetailEvent(eventId)
                _eventDetail.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Log.e("DetailViewModel", "Error fetching event details", e)
            } finally {
                _isLoading.value = false // Menghentikan loading
            }
        }
    }
}