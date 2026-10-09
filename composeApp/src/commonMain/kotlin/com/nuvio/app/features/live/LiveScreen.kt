package com.nuvio.app.features.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * LIVE tab: Smartcric live cricket inside the app,
 * with full ad blocking (network + DOM level).
 */
@Composable
internal fun LiveScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        LiveStreamWebView(
            url = SMARTCRIC_URL,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
