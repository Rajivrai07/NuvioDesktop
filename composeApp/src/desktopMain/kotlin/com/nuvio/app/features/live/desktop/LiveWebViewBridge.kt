package com.nuvio.app.features.live.desktop

import com.nuvio.app.features.player.desktop.NativePlayerBridge

/**
 * JNI bridge to the native standalone WebView2 (Windows) used by the LIVE tab.
 * The native side hosts a Chromium WebView2 as a child of the AWT host window,
 * with network-level ad blocking and JS ad cleanup.
 *
 * The JNI functions live in the same native library as the player bridge,
 * so touching [NativePlayerBridge] ensures the DLL is loaded.
 */
internal object LiveWebViewBridge {
    init {
        // Force-load the native library via the player bridge (same DLL).
        runCatching { NativePlayerBridge.toString() }
    }

    external fun createLiveWebView(hostViewPtr: Long, startUrl: String): Long
    external fun disposeLiveWebView(handle: Long)
    external fun layoutLiveWebView(handle: Long)
}
