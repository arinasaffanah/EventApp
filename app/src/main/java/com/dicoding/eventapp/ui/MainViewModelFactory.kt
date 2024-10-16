package com.dicoding.eventapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.eventapp.data.repository.EventRepository


class MainViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") // Menghindari peringatan casting
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}