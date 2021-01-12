package de.famprobst.report.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.famprobst.report.R

class ActivityInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_information)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title
        supportActionBar?.title = getString(R.string.activityInformation_Title)

        // Set subtitle
        supportActionBar?.subtitle = getString(R.string.activityInformation_SubTitle)
    }

}