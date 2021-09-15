package de.famprobst.report.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.famprobst.report.R

class ActivityPartner : AppCompatActivity() {

    private lateinit var imageAd1: ImageView
    private lateinit var imageAd2: ImageView
    private lateinit var imageAd3: ImageView
    private lateinit var imageAd4: ImageView
    private lateinit var imageAd5: ImageView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_partner)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title
        supportActionBar?.title = getString(R.string.activityPartner_Title)

        // Get all views
        imageAd1 = findViewById(R.id.activityPartner_Image1)
        imageAd2 = findViewById(R.id.activityPartner_Image2)
        imageAd3 = findViewById(R.id.activityPartner_Image3)
        imageAd4 = findViewById(R.id.activityPartner_Image4)
        imageAd5 = findViewById(R.id.activityPartner_Image5)
        button = findViewById(R.id.activityPartner_Button)

        // Set all links
        setupPartnerLinks()
    }

    private fun setupPartnerLinks() {
        // Feinwerkbau
        imageAd1.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.feinwerkbau.de")
                )
            )
        }

        // Sauer
        imageAd2.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.sauer-shootingsportswear.de")
                )
            )
        }

        // Markus Koch
        imageAd3.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://coaching-koch.de/")))
        }

        // Disag
        imageAd4.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.disag.de/")))
        }

        // Tec-Hro
        imageAd5.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://tec-hro.de/schiesssport/de")
                )
            )
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