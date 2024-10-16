package com.dicoding.eventapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.eventapp.data.response.EventResponse
import com.dicoding.eventapp.data.retrofit.ApiConfig
import com.dicoding.eventapp.utils.Events
import kotlinx.coroutines.launch
import com.dicoding.eventapp.data.response.ListEventsItem
import kotlinx.coroutines.async
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class MainViewModel : ViewModel() {
    private val _eventList = MutableLiveData<EventResponse>()
    val eventList: LiveData<EventResponse?> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _snackBar = MutableLiveData<Events<String>>()
    val snackBar: LiveData<Events<String>> get() = _snackBar

    private val _eventDetails = MutableLiveData<ListEventsItem?>() // Ubah tipe menjadi ListEventsItem
    val eventDetails: LiveData<ListEventsItem?> get() = _eventDetails

    private val _finishedEventList = MutableLiveData<EventResponse>()
    val finishedEventList: LiveData<EventResponse> get() = _finishedEventList

    private val _upcomingEventList = MutableLiveData<EventResponse>()  // Upcoming events
    val upcomingEventList: LiveData<EventResponse> get() = _upcomingEventList

    companion object {
        private const val TAG = "MainViewModel"
    }

//    val combinedEventList: LiveData<List<ListEventsItem>> = MediatorLiveData<List<ListEventsItem>>().apply {
//        addSource(_upcomingEventList) { upcomingEvents ->
//            value = combineEvents(upcomingEvents.listEvents, _finishedEventList.value?.listEvents ?: emptyList())
//        }
//        addSource(_finishedEventList) { finishedEvents ->
//            value = combineEvents(_upcomingEventList.value?.listEvents ?: emptyList(), finishedEvents.listEvents)
//        }
//    }
//
//    fun combineEvents(upcoming: List<ListEventsItem>, finished: List<ListEventsItem>): List<ListEventsItem> {
//        return upcoming + finished // Merging the two lists
//    }

    fun getActiveEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getActiveEvents() // Panggil metode suspend langsung
                _eventList.value = response // Mengupdate LiveData dengan daftar acara
                _upcomingEventList.value = response  // Update upcoming events
            } catch (e: Exception) {
                _snackBar.value = Events("Terjadi kesalahan: ${e.message}")
                Log.e(TAG, "Error fetching active events: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPastEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getPastEvents() // Panggil metode suspend langsung
                _eventList.value = response // Update LiveData
            } catch (e: Exception) {
                _snackBar.value = Events("Terjadi kesalahan saat mengambil acara.")
                Log.e(TAG, "Error fetching past events: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Start both API calls concurrently
                val activeEventsDeferred = async { ApiConfig.getApiService().getActiveEvents() }
                val pastEventsDeferred = async { ApiConfig.getApiService().getPastEvents() }

                // Await both results
                val activeEventsResponse = activeEventsDeferred.await()
                val pastEventsResponse = pastEventsDeferred.await()

                // Update LiveData
                _upcomingEventList.value = activeEventsResponse
                _finishedEventList.value = pastEventsResponse
                _eventList.value = EventResponse(
                    listEvents = activeEventsResponse.listEvents + pastEventsResponse.listEvents,
                    // Include other properties as needed
                )
            } catch (e: Exception) {
                _snackBar.value = Events("Terjadi kesalahan: ${e.message}")
                Log.e(TAG, "Error fetching events: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }


//    fun getEventDetails(eventId: String) {
//        viewModelScope.launch {
//            try {
//                val response = ApiConfig.getApiService().getEventDetails(eventId)
//                val eventDetail = response.listEvents.find { it.id == eventId.toInt() } // Mengambil detail acara dari listEvents
//                _eventDetails.value = eventDetail // Update LiveData dengan detail acara
//            } catch (e: Exception) {
//                _snackBar.value = Events("Terjadi kesalahan saat mengambil detail acara.")
//            }
//        }
//    }
}