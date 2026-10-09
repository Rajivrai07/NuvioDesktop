package com.nuvio.app.features.live

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Smartcric live cricket site, shown inside the app. */
internal const val SMARTCRIC_URL = "https://smartcric.is/"

/**
 * Platform WebView for LIVE streams with built-in ad blocking.
 * - Windows: WebView2 (Chromium)
 * - Linux: WebKitGTK
 * - macOS: WKWebView
 */
@Composable
internal expect fun LiveStreamWebView(
    url: String,
    modifier: Modifier,
)
