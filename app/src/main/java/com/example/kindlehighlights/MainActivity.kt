package com.example.kindlehighlights

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kindlehighlights.ui.theme.KindleHighlightsTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KindleHighlightsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WebViewScreen()
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled", "NewApi", "JavascriptInterface")
@Composable
fun WebViewScreen() {
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)  // Ensure cookies are accepted

    val jInterface = JavaScriptInterface { ids ->
        ids.forEach {
            println("Loaded data: $it")
        }
        ids.first()
        callApiWithCookies(
            "https://read.amazon.com/notebook?asin=${ids.first()}&contentLimitState=&",
            cookieManager
        )
    }


    AndroidView(
        factory = { context ->
            WebView(context).apply {
                addJavascriptInterface(jInterface, "HtmlViewer")
                webViewClient = CustomWebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.loadsImagesAutomatically = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportZoom(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)
            }
        },
        update = { webView ->
            webView.loadUrl("https://read.amazon.com/notebook")
            Log.i("MainActivity", "WebViewScreen : html is ->" + jInterface.htmlValue)
        },
        onRelease = { webView ->
            Log.i("MainActivity", "WebViewScreen : html is ->" + jInterface.htmlValue)
        }
    )


}

fun callApiWithCookies(url: String, cookieManager: CookieManager) {
    val cookiesString = cookieManager.getCookie(url)
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Cookie", cookiesString)
                .build()
            chain.proceed(request)
        }
        .build()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("API CALL", "onFailure: ")
        }

        override fun onResponse(call: Call, response: Response) {
            HighlightInterface().readHtml(response.body?.string() ?: "")
        }
    })
}

class CustomWebViewClient : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.i("MainActivity", "onPageFinished: $url")
        view?.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
    }

}
