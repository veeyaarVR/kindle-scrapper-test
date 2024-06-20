package com.example.kindlehighlights

import android.util.Log
import android.webkit.JavascriptInterface
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class JavaScriptInterface(var callback: (bookIDs: List<String>) -> Unit) {

    var htmlValue: String? = null
    var receivedBookIds: MutableList<String> = mutableListOf();

    @JavascriptInterface
    fun showHTML(html: String) {
        htmlValue = html
        Thread {
            val builder = StringBuilder()
            try {

                val doc: Document = Jsoup.parse(html)
                val baseElement: Element? = doc.body().getElementById("a-page")

                val libraryElement =
                    baseElement?.getElementById("kp-notebook-library")

                if (libraryElement == null) {
                    println("libraryELement null")
                } else {
                    Log.i(
                        "MainActivity",
                        "getWebsite: libraryElement -> " + libraryElement.children().size
                    )
                }

                libraryElement?.children()?.forEach {
                    Log.i("MainActivity", "bookId: " + it.id())
                    receivedBookIds.add(it.id())
                    it.getElementsByClass("a-link-normal a-text-normal").forEach {sub->
                        Log.i("MainActivity", "bookName: " + sub.select("h2").text())
                    }
                }
                callback(receivedBookIds)
            } catch (e: Exception) {
                builder.append("Error : ").append(e.message).append("\n")
            }
        }.start()
    }
}
