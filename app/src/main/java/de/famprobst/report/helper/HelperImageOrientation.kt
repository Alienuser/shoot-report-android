package de.famprobst.report.helper

import android.media.ExifInterface
import java.io.File

object HelperImageOrientation {

    fun getTheImageOrientation(imagePath: File): Int {
        // Get the orientation of the image
        val ei = ExifInterface(imagePath.absolutePath)
        return ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }
}