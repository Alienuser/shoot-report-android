package de.famprobst.report.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.famprobst.report.R

class ActivityCooperation : AppCompatActivity() {

    private lateinit var imageAd1: ImageView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_cooperation)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title
        supportActionBar?.title = getString(R.string.activityCooperation_Title)

        // Get all views
        imageAd1 = findViewById(R.id.activityCooperation_Image1)
        button = findViewById(R.id.activityCooperation_Button)

        // Set all links
        setupPartnerLinks()
    }

    private fun setupPartnerLinks() {
        // KKSV
        imageAd1.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kksvillingen.de/")))
        }

        // Contact
        button.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.burkhardt-sport.solutions/kontakt")
                )
            )
        }
    }
}