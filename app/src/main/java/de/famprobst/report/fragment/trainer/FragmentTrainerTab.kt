package de.famprobst.report.fragment.trainer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import de.famprobst.report.R
import java.util.*

class FragmentTrainerTab(private val tabNumber: Int) : Fragment() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private val hostingUrl = "https://trainer.burkhardt-sport.solutions"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Set the default view
        val layout = inflater.inflate(R.layout.fragment_trainer_tabs, container, false)

        // Get the layout
        webView = layout.findViewById(R.id.fragmentTrainer_webView)
        progressBar = layout.findViewById(R.id.fragmentTrainer_progressBar)

        // Set the webView client
        webView.webViewClient = WebViewClient()

        // Display the html file in the web viewer
        displayContent()

        // Return the layout
        return layout
    }

    private fun displayContent() {
        // Get the right language
        val locale: String = Locale.getDefault().language

        // Get the right content
        val content = when (tabNumber) {
            1 -> "equipment_cloths.html"
            2 -> "equipment_sport.html"
            3 -> "equipment_equipment.html"
            4 -> "tech_positioning.html"
            5 -> "tech_procedure.html"
            6 -> "mental_relax.html"
            7 -> "mental_motivation.html"
            8 -> "mental_focus.html"
            else -> ""
        }

        // Load the page
        webView.loadUrl("$hostingUrl/$locale/$content")
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("http://") || url.contains("https://")) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else {
                view.loadUrl(url)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }
}