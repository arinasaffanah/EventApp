package com.dicoding.eventapp.data.retrofit

import com.dicoding.eventapp.data.response.EventResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @GET("events/{id}")
    suspend fun getEventDetails(@Path("id") eventId: String): EventResponse

    companion object {
        private const val BASE_URL = "https://event-api.dicoding.dev/"

        // Method to create an instance of ApiService
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}