package com.example.tunebox.data.models

data class SearchResponse(
    val tracks: TracksSearchResponse,
    val albums: AlbumsSearchResponse
)

data class TracksSearchResponse(
    val items: List<SpotifyTrack>
)

data class AlbumsSearchResponse(
    val items: List<SpotifyAlbum>
)
