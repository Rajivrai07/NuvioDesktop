package com.nuvio.app.features.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.nuvio.app.features.live.desktop.LiveWebViewBridge
import com.nuvio.app.features.player.desktop.AwtNativeViewResolver
import java.awt.Canvas
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Desktop actual: hosts Smartcric in a native WebView2 (Windows) child window
 * with full ad blocking. Other desktop platforms show a notice for now.
 */
@Composable
internal actual fun LiveStreamWebView(
    url: String,
    modifier: Modifier,
) {
    val hostOs = remember { System.getProperty("os.name", "").lowercase() }
    val isWindows = hostOs.contains("win")

    if (!isWindows) {
        // Linux/macOS native webview is a follow-up; show a clear message.
        androidx.compose.foundation.layout.Box(
            modifier = modifier,
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            androidx.compose.material3.Text(
                "LIVE is currently supported on Windows only.",
                color = androidx.compose.ui.graphics.Color.White,
            )
        }
        return
    }

    var handle by remember { mutableStateOf(0L) }
    var hostCanvas by remember { mutableStateOf<Canvas?>(null) }

    SwingPanel(
        factory = {
            Canvas().also { canvas ->
                hostCanvas = canvas
                canvas.addComponentListener(object : ComponentAdapter() {
                    override fun componentResized(e: ComponentEvent?) {
                        val h = handle
                        if (h != 0L) {
                            runCatching { LiveWebViewBridge.layoutLiveWebView(h) }
                        }
                    }
                })
            }
        },
        modifier = modifier,
        background = androidx.compose.ui.graphics.Color.Black,
    )

    DisposableEffect(hostCanvas, url) {
        val canvas = hostCanvas ?: return@DisposableEffect onDispose {}
        val job = CoroutineScope(Dispatchers.IO).launch {
            // Wait for the AWT peer to be ready, then create the native view.
            var hwnd = 0L
            repeat(50) {
                hwnd = runCatching { AwtNativeViewResolver.resolveNativeViewPointer(canvas) }.getOrDefault(0L)
                if (hwnd != 0L) return@repeat
                delay(100)
            }
            if (hwnd == 0L) return@launch
            val created = runCatching { LiveWebViewBridge.createLiveWebView(hwnd, url) }.getOrDefault(0L)
            if (created != 0L) {
                withContext(Dispatchers.Main) { handle = created }
            }
        }
        onDispose {
            job.cancel()
            val h = handle
            if (h != 0L) {
                runCatching { LiveWebViewBridge.disposeLiveWebView(h) }
                handle = 0L
            }
            hostCanvas = null
        }
    }
}
