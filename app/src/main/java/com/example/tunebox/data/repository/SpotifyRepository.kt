package com.example.tunebox.data.repository

import com.example.tunebox.data.api.SpotifyApiService
import com.example.tunebox.data.models.SpotifyAlbum
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
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMostListenedAlbums(accessToken: String): List<SpotifyAlbum> {
        return try {
            val response = apiService.getTopTracks(
                authorization = "Bearer $accessToken",
                limit = 50
            )
            // Extrai os álbuns únicos das tracks mais ouvidas
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
}
