package com.example.tunebox.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tunebox.SpotifyConstants

@Composable
fun LoginWithSpotifyScreen(
    onCodeReceived: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()

                            if (url.startsWith(SpotifyConstants.REDIRECT_URI)) {
                                val code = url
                                    .substringAfter("code=")
                                    .substringBefore("&")

                                if (code.isNotEmpty()) {
                                    onCodeReceived(code)
                                }
                                return true
                            }
                            return false
                        }
                    }

                    loadUrl(SpotifyConstants.buildAuthUrl())
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
