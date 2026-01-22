package com.example.tunebox

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.tunebox.data.manager.TokenManager
import com.example.tunebox.data.repository.SpotifyAuthRepository
import com.example.tunebox.di.appModule
import com.example.tunebox.notifications.NotificationManager
import com.example.tunebox.notifications.NotificationScheduler
import com.example.tunebox.screens.AuthScreen
import com.example.tunebox.screens.HomeScreen
import com.example.tunebox.screens.LoginWithSpotifyScreen
import com.example.tunebox.ui.theme.TuneBoxTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MainActivity : ComponentActivity() {

    private val tokenManager: TokenManager by inject()
    private val notificationManager: NotificationManager by inject()
    private val notificationScheduler: NotificationScheduler by inject()
    private val authRepository: SpotifyAuthRepository by inject()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            startKoin {
                androidContext(this@MainActivity)
                modules(appModule)
            }
        } catch (e: Exception) { }

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            var currentScreen by rememberSaveable { mutableStateOf("auth") }
            var accessToken by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(currentScreen) {
                if (currentScreen == "auth") {
                    delay(1000)
                    askNotificationPermission()
                }
            }

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
                                        val response = authRepository.exchangeCodeForToken(code)
                                        if (response != null) {
                                            tokenManager.saveTokens(
                                                accessToken = response.access_token,
                                                refreshToken = response.refresh_token
                                            )
                                            accessToken = response.access_token
                                            currentScreen = "home"

                                            notificationScheduler.scheduleDailyReminder(
                                                title = "Hora de ouvir música",
                                                message = "Ouça suas músicas favoritas",
                                                hourOfDay = 8
                                            )

                                            notificationManager.showLocalNotification(
                                                "Bem-vindo!",
                                                "Seu login foi bem-sucedido"
                                            )

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
                                notificationManager = notificationManager,
                                onLogout = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        tokenManager.clearTokens()
                                        notificationScheduler.cancelAllReminders()
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
            }
        }
    }
}
