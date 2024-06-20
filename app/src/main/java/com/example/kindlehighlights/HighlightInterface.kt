package com.example.kindlehighlights

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class HighlightInterface {
    fun readHtml(html: String) {
        Thread {
            val builder = StringBuilder()
            try {

                val doc: Document = Jsoup.parse(html)
                val baseElement: Element? = doc.body().getElementById("a-page")

                val libraryElement =
                    baseElement?.getElementById("kp-notebook-annotations")

                if (libraryElement == null) {
                    println("libraryELement null")
                } else {
                    Log.i(
                        "HighlightInterface",
                        "getWebsite: libraryElement -> " + libraryElement.children().size
                    )
                }

                libraryElement?.children()?.forEach {
                    val highlight = it.getElementById("highlight")?.text()
                    Log.i("HighlightInterface", "getHighlight: " + highlight)
                }

            } catch (e: Exception) {
                builder.append("Error : ").append(e.message).append("\n")
            }
        }.start()
    }
}