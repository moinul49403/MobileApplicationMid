package com.example.learningportalapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var etUrl: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnRefresh: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnGo: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnYouTube: Button
    private lateinit var btnWikipedia: Button
    private lateinit var btnKhan: Button
    private lateinit var btnUniversity: Button
    private val homeUrl: String = "https://www.google.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        etUrl = findViewById(R.id.etUrl)
        progressBar = findViewById(R.id.progressBar)
        btnBack = findViewById(R.id.btnBack)
        btnForward = findViewById(R.id.btnForward)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnHome = findViewById(R.id.btnHome)
        btnGo = findViewById(R.id.btnGo)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnYouTube = findViewById(R.id.btnYouTube)
        btnWikipedia = findViewById(R.id.btnWikipedia)
        btnKhan = findViewById(R.id.btnKhan)
        btnUniversity = findViewById(R.id.btnUniversity)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                progressBar.progress = 100
                etUrl.setText(url ?: "")
                updateNavigationButtons()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    webView.loadUrl("file:///android_asset/offline.html")
                    Toast.makeText(
                        this@MainActivity,
                        "No internet connection - showing offline page",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            }
        }

        loadUrl(homeUrl)

        btnBack.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                Toast.makeText(this, "No more history", Toast.LENGTH_SHORT).show()
            }
        }

        btnForward.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        btnRefresh.setOnClickListener {
            webView.reload()
        }

        btnHome.setOnClickListener {
            loadUrl(homeUrl)
        }

        btnGo.setOnClickListener {
            val urlText = etUrl.text.toString().trim()
            if (urlText.isNotEmpty()) {
                loadUrl(urlText)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }

        etUrl.setOnEditorActionListener { _, actionId, event ->
            val isGoAction = actionId == EditorInfo.IME_ACTION_GO
            val isEnterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN

            if (isGoAction || isEnterKey) {
                val urlText = etUrl.text.toString().trim()
                if (urlText.isNotEmpty()) {
                    loadUrl(urlText)
                } else {
                    Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        btnGoogle.setOnClickListener { loadUrl("https://www.google.com") }
        btnYouTube.setOnClickListener { loadUrl("https://www.youtube.com") }
        btnWikipedia.setOnClickListener { loadUrl("https://www.wikipedia.org") }
        btnKhan.setOnClickListener { loadUrl("https://www.khanacademy.org") }
        btnUniversity.setOnClickListener { loadUrl("https://www.coursera.org") }

        updateNavigationButtons()
    }

    private fun loadUrl(url: String) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isEmpty()) {
            return
        }
        val validUrl = if (
            trimmedUrl.startsWith("http://", ignoreCase = true) ||
            trimmedUrl.startsWith("https://", ignoreCase = true)
        ) {
            trimmedUrl
        } else {
            "https://$trimmedUrl"
        }
        webView.loadUrl(validUrl)
        etUrl.setText(validUrl)
    }

    private fun updateNavigationButtons() {
        val canGoBack = webView.canGoBack()
        val canGoForward = webView.canGoForward()
        btnBack.isEnabled = canGoBack
        btnForward.isEnabled = canGoForward
        btnBack.alpha = if (canGoBack) 1.0f else 0.5f
        btnForward.alpha = if (canGoForward) 1.0f else 0.5f
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}