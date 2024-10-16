package com.dicoding.eventapp.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpcomingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is upcoming Fragment"
    }
    val text: LiveData<String> = _text
}