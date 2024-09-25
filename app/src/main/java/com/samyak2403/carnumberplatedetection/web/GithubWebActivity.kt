/*
 * Created by Samyak Kamble on 9/25/24, 11:33 PM
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 9/25/24, 11:33 PM
 */

package com.samyak2403.carnumberplatedetection.web

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.samyak2403.carnumberplatedetection.R

class GithubWebActivity : AppCompatActivity() {

    private lateinit var githubWebViewHelper: GithubWebView

    /** 📄🚫 No changes allowed in this code! Please use as-is. 🚫📄**/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_webview)
        // Find the toolbar by its ID
        val toolbar: Toolbar = findViewById(R.id.toolbar)


        // Set the toolbar
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        // Set up the WebView and ensure it is focusable for D-pad navigation
        val webView: WebView = findViewById(R.id.webView)
        webView.isFocusable = true
        webView.isFocusableInTouchMode = true

        // Use the GithubWebView class to initialize the WebView
        githubWebViewHelper = GithubWebView(this, webView)
        githubWebViewHelper.initializeWebView("https://github.com/samyak2403/")
    }

    // Override key event to manage D-pad navigation within the WebView
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val webView: WebView = findViewById(R.id.webView)
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> webView.pageUp(true)  // Navigate up
            KeyEvent.KEYCODE_DPAD_RIGHT -> webView.pageDown(true) // Navigate down
            KeyEvent.KEYCODE_DPAD_UP -> webView.pageUp(false)  // Navigate up
            KeyEvent.KEYCODE_DPAD_DOWN -> webView.pageDown(false)  // Navigate down
        }
        return super.onKeyDown(keyCode, event)
    }
}
