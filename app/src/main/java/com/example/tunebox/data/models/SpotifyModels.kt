package com.example.tunebox.data.models

data class SpotifyAuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String? = null
)
data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)
data class SpotifyArtist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>?
)
data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val artists: List<SpotifyArtist>
)
data class SpotifyTrack(
    val id: String,
    val name: String,
    val album: SpotifyAlbum,
    val artists: List<SpotifyArtist>,
    val duration_ms: Int
)
data class SpotifyPlaylistsResponse(
    val items: List<SpotifyPlaylist>,
    val next: String?
)
data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val description: String?
)
data class SpotifyTracksResponse(
    val items: List<SpotifyTrack>,
    val next: String?
)
data class SpotifyNewReleasesResponse(
    val albums: SpotifyAlbumsData
)
data class SpotifyAlbumsData(
    val items: List<SpotifyAlbum>,
    val next: String?
)
data class SpotifyUserResponse(
    val id: String,
    val display_name: String?,
    val images: List<SpotifyImage>?
)
data class SpotifyUser(
    val id: String,
    val displayName: String,
    val imageUrl: String?
)
fun SpotifyUserResponse.toDomain(): SpotifyUser =
    SpotifyUser(
        id = id,
        displayName = display_name ?: "User",
        imageUrl = images?.firstOrNull()?.url
    )
data class TopTracksResponse(
    val items: List<TrackItem>
)
data class TrackItem(
    val name: String,
    val album: Album
)
data class Album(
    val images: List<SpotifyImage>
)
data class PlaylistsResponse(
    val items: List<PlaylistItem>
)
data class PlaylistItem(
    val name: String,
    val images: List<SpotifyImage>
)
