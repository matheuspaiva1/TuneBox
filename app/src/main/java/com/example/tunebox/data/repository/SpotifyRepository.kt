package com.example.tunebox.data.repository

import com.example.tunebox.data.api.SpotifyApiService
import com.example.tunebox.data.models.SpotifyAlbum
import com.example.tunebox.data.models.SpotifyTrack
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.tunebox.data.models.SpotifyUser
import com.example.tunebox.data.models.toDomain
class SpotifyRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(SpotifyApiService::class.java)

    suspend fun getCurrentUser(accessToken: String): SpotifyUser? {
        return try {
            println("SPOTIFY getCurrentUser() token = $accessToken")

            val response = apiService.getCurrentUser(
                authorization = "Bearer $accessToken"
            )
            println("SPOTIFY USER RESPONSE: $response")

            response.toDomain()
        } catch (e: Exception) {
            println("SPOTIFY USER ERROR: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun getTopTracks(accessToken: String): List<SpotifyTrack> {
        return try {
            val response = apiService.getTopTracksFull(
                authorization = "Bearer $accessToken",
                limit = 50
            )
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMostListenedAlbums(accessToken: String): List<SpotifyAlbum> {
        return try {
            val response = apiService.getTopTracksFull(
                authorization = "Bearer $accessToken",
                limit = 50
            )

            response.items
                .map { it.album }
                .distinctBy { it.id }
                .take(20)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getNewReleases(accessToken: String): List<SpotifyAlbum> {
        return try {
            val response = apiService.getNewReleases(
                authorization = "Bearer $accessToken",
                limit = 20
            )
            response.albums.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserTopTracksShort(accessToken: String, limit: Int = 3) =
        try {
            val response = apiService.getTopTracksShort(
                authorization = "Bearer $accessToken",
                limit = limit,
                timeRange = "short_term"
            )
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    suspend fun getUserPlaylistsShort(accessToken: String, limit: Int = 3) =
        try {
            val response = apiService.getUserPlaylistsShort(
                authorization = "Bearer $accessToken",
                limit = limit
            )
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
}
