package de.famprobst.report.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivitySplashScreen : AppCompatActivity() {

    private val splashTimeOut: Long = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(splashTimeOut)
            startActivity(Intent(this@ActivitySplashScreen, ActivityRifle::class.java))
            finish()
        }
    }
}