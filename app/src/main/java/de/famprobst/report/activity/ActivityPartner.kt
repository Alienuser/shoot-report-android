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

        // Feinwerkbau
        findViewById<ImageView>(R.id.activityPartner_Image1).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.feinwerkbau.de")))
        }

        // Sauer
        findViewById<ImageView>(R.id.activityPartner_Image2).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.sauer-shootingsportswear.de")
                )
            )
        }

        // Markus Koch
        findViewById<ImageView>(R.id.activityPartner_Image3).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://coaching-koch.de/")))
        }

        // Disag
        findViewById<ImageView>(R.id.activityPartner_Image4).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.disag.de/")))
        }

        // Tec-Hro
        findViewById<ImageView>(R.id.activityPartner_Image5).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://tec-hro.de/schiesssport/de")
                )
            )
        }

        // KKSV
        findViewById<ImageView>(R.id.activityPartner_Image6).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kksvillingen.de/")))
        }

        // Contact
        findViewById<Button>(R.id.activityPartner_Button).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.burkhardt-sport.solutions/kontakt")
                )
            )
        }
    }

}