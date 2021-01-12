package de.famprobst.report.fragment.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import de.famprobst.report.R
import java.util.*

class FragmentTrainerTab(private val tabNumber: Int) : Fragment() {

    private lateinit var layout: View
    private lateinit var content: String
    private lateinit var webview: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Set the default view
        layout = inflater.inflate(R.layout.fragment_trainer_tabs, container, false)

        // Get the right layout
        when (tabNumber) {
            1 -> {
                content = "equipment_cloths.html"
            }
            2 -> {
                content = "equipment_sport.html"
            }
            3 -> {
                content = "equipment_equipment.html"
            }
            4 -> {
                content = "tech_attack.html"
            }
            5 -> {
                content = "tech_procedure.html"
            }
            6 -> {
                content = "mental_relax.html"
            }
            7 -> {
                content = "mental_motivation.html"
            }
            8 -> {
                content = "mental_focus.html"
            }
        }

        // Display the html file in the web viewer
        displayContent()

        // Return the layout
        return layout
    }

    private fun displayContent() {
        val locale = if (Locale.getDefault().language == "de") {
            "de"
        } else {
            "en"
        }
        webview = layout.findViewById(R.id.fragmentTrainer_webView)
        webview.loadUrl("file:///android_asset/coach/$locale/$content")
    }
}