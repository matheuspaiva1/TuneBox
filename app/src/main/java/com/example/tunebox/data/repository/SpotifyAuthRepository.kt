package com.example.tunebox.data.repository

import com.example.tunebox.SpotifyConstants
import com.example.tunebox.data.api.SpotifyAuthService
import com.example.tunebox.data.models.SpotifyAuthResponse
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyAuthRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl(SpotifyConstants.AUTH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(SpotifyAuthService::class.java)

    suspend fun exchangeCodeForToken(code: String): SpotifyAuthResponse? {
        return try {
            authService.getAccessToken(
                grantType = "authorization_code",
                code = code,
                redirectUri = SpotifyConstants.REDIRECT_URI,
                clientId = SpotifyConstants.CLIENT_ID,
                clientSecret = SpotifyConstants.CLIENT_SECRET
            )
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            println("SPOTIFY TOKEN ERROR ${e.code()}: $body")
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
