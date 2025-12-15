package com.example.tunebox

object SpotifyConstants {
    const val CLIENT_ID = "5ec693dbe5154b03845d08d89be18eb9"
    const val CLIENT_SECRET = "a8720122d79145e6a49cbbadfbf2e3c0"   // inseguro em produção
    const val REDIRECT_URI = "tunebox://callback"
    const val SCOPES = "user-top-read playlist-read-private user-library-read"

    const val AUTH_BASE_URL = "https://accounts.spotify.com"
    const val API_BASE_URL = "https://api.spotify.com/"

    fun buildAuthUrl(): String =
        "$AUTH_BASE_URL/authorize?" +
                "client_id=$CLIENT_ID&" +
                "response_type=code&" +
                "redirect_uri=$REDIRECT_URI&" +
                "scope=${SCOPES.replace(" ", "%20")}"
}
