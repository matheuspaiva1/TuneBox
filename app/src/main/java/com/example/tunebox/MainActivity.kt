package com.example.tunebox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tunebox.data.manager.TokenManager
import com.example.tunebox.data.repository.SpotifyAuthRepository
import com.example.tunebox.screens.AuthScreen
import com.example.tunebox.screens.HomeScreen
import com.example.tunebox.screens.LoginWithSpotifyScreen
import com.example.tunebox.ui.theme.TuneBoxTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager
    private val authRepository = SpotifyAuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            var currentScreen by rememberSaveable { mutableStateOf("auth") } // "auth", "spotify_login", "home"
            var accessToken by rememberSaveable { mutableStateOf("") }

            TuneBoxTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "auth" -> {
                            AuthScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                onLoginWithSpotify = {
                                    currentScreen = "spotify_login"
                                }
                            )
                        }

                        "spotify_login" -> {
                            LoginWithSpotifyScreen(
                                onCodeReceived = { code ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        println("SPOTIFY CODE: $code")
                                        val response = authRepository.exchangeCodeForToken(code)
                                        if (response != null) {
                                            tokenManager.saveTokens(
                                                accessToken = response.access_token,
                                                refreshToken = response.refresh_token
                                            )
                                            accessToken = response.access_token
                                            currentScreen = "home"
                                        } else {
                                            currentScreen = "auth"
                                        }
                                    }
                                }
                            )
                        }

                        "home" -> {
                            HomeScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                accessToken = accessToken,
                                onLogout = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        tokenManager.clearTokens()
                                        accessToken = ""
                                        currentScreen = "auth"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Detecta quando o Spotify redireciona de volta para o app
        handleSpotifyCallback(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSpotifyCallback(intent)
    }

    private fun handleSpotifyCallback(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "tunebox" && data.host == "callback") {
            val code = data.getQueryParameter("code")
            if (code != null) {
                // O código já é processado na LoginWithSpotifyScreen
            }
        }
    }
}
