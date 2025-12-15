package com.example.tunebox.data.repository

import com.example.tunebox.data.api.SpotifyApiService
import com.example.tunebox.data.models.SpotifyTrack
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(SpotifyApiService::class.java)

    suspend fun getTopTracks(accessToken: String): List<SpotifyTrack> {
        return try {
            val response = apiService.getTopTracks(
                authorization = "Bearer $accessToken",
                limit = 50
            )
            response.items
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNewReleases(accessToken: String) = try {
        val response = apiService.getNewReleases(
            authorization = "Bearer $accessToken",
            limit = 20
        )
        response.albums.items
    } catch (e: Exception) {
        emptyList()
    }
}
