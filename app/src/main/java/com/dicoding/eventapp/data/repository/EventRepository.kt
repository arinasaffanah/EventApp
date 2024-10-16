package com.dicoding.eventapp.data.repository

import com.dicoding.eventapp.data.response.ListEventsItem
import com.dicoding.eventapp.data.retrofit.ApiService

class EventRepository(private val apiService: ApiService) {
    suspend fun getEventById(eventId: String): ListEventsItem? {
        val response = apiService.getEventDetails(eventId)
        return response.listEvents.find { it.id == eventId.toInt() }
    }

    suspend fun getEventDetails(eventId: String): ListEventsItem? {
        val response = apiService.getEventDetails(eventId) // Mengembalikan EventResponse
        return response.listEvents.find { it.id.toString() == eventId } // Mengembalikan ListEventsItem
    }
}