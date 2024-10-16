package com.dicoding.eventapp.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FinishedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is finished Fragment"
    }
    val text: LiveData<String> = _text
}