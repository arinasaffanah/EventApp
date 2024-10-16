package com.dicoding.eventapp.data.retrofit

import android.telecom.Call
import com.dicoding.eventapp.data.response.DetailEventResponse
import com.dicoding.eventapp.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Get active events
    @GET("events")
    suspend fun getActiveEvents(@Query("active") active: Int = 1): EventResponse

    // Get past events
    @GET("events")
    suspend fun getPastEvents(@Query("active") active: Int = 0): EventResponse

    // Search for events
    @GET("events")
    suspend fun searchEvents(@Query("active") active: Int = -1, @Query("q") keyword: String): EventResponse

    // Get event details by ID
    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: String): DetailEventResponse

//    @GET("events?active=0")
//    suspend fun getFinishedEvents(): EventResponse

}