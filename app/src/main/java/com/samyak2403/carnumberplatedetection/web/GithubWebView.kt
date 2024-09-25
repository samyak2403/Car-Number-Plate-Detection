/*
 * Created by Samyak Kamble on 9/25/24, 11:43 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 9/25/24, 11:43 PM
 */

package com.samyak2403.carnumberplatedetection.web

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class GithubWebView(private val context: Context, private val webView: WebView) {

    /** 📄🚫 No changes allowed in this code! Please use as-is. 🚫📄**/

    fun initializeWebView(url: String) {
        // Configure the WebView settings
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true  // Enable JavaScript if required

        // Set WebView client to open links within the WebView instead of a browser
        webView.webViewClient = WebViewClient()

        // Load the desired URL
        webView.loadUrl(url)
    }
}
