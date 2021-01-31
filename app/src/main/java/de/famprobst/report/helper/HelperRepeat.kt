package de.famprobst.report.helper

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import de.famprobst.report.R
import kotlinx.coroutines.*

object HelperRepeat {

    private lateinit var job: Job
    private const val delay = 5000L
    private var adNumber = 0

    fun startRepeat(view: View, context: Context) {
        adNumber = (1..5).random()
        this.job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    switchAdImage(view, context)
                }
                delay(delay)
            }
        }
    }

    fun stopRepeat() {
        job.cancel()
    }

    private fun switchAdImage(view: View, context: Context) {
        // Select a random image
        when (adNumber) {
            1 -> view.findViewById<ImageView>(R.id.ads)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_advertising_1))
            2 -> view.findViewById<ImageView>(R.id.ads)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_advertising_2))
            3 -> view.findViewById<ImageView>(R.id.ads)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_advertising_3))
            4 -> view.findViewById<ImageView>(R.id.ads)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_advertising_4))
            5 -> view.findViewById<ImageView>(R.id.ads)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_advertising_5))
        }

        if (adNumber >= 5) {
            adNumber = 1
        } else {
            adNumber++
        }
    }

}