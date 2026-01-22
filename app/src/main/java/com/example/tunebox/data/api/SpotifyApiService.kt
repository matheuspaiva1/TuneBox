package com.example.tunebox.data.api

import com.example.tunebox.data.models.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/me/top/tracks")
    suspend fun getTopTracksFull(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): SpotifyTracksResponse

    @GET("v1/me/playlists")
    suspend fun getUserPlaylistsFull(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 50
    ): SpotifyPlaylistsResponse
    @GET("v1/browse/new-releases")
    suspend fun getNewReleases(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 20
    ): SpotifyNewReleasesResponse

    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String,
        @Query("limit") limit: Int = 50
    ): SpotifyTracksResponse
    @GET("v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): SpotifyUserResponse
    @GET("v1/me/top/tracks")
    suspend fun getTopTracksShort(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 3,
        @Query("time_range") timeRange: String = "short_term"
    ): TopTracksResponse
    @GET("v1/me/playlists")
    suspend fun getUserPlaylistsShort(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 3
    ): PlaylistsResponse

    @GET("v1/search")
    suspend fun search(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("type") type: String = "track,album",
        @Query("limit") limit: Int = 20
    ): SearchResponse
}