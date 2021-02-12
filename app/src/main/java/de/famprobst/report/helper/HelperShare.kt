package de.famprobst.report.helper

import android.content.Context
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.entity.EntryTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object HelperShare {

    fun shareTraining(
        training: EntryTraining,
        rifleName: String?,
        sharePath: File?,
        context: Context
    ): File {

        // Create the file
        val shareFile = createFile(sharePath)

        // Header
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputWeapon)}; " +
                    "${context.getString(R.string.activityDetails_InputIndicator)}; " +
                    "${context.getString(R.string.activityDetails_InputKind)}; " +
                    "${context.getString(R.string.activityDetails_InputPlace)}; " +
                    "${context.getString(R.string.activityDetails_InputDate)}; " +
                    "${context.getString(R.string.activityDetails_InputCount)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints1)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints2)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints3)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints4)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints5)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints6)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints7)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints8)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints9)}; " +
                    "${context.getString(R.string.activityDetails_Points)}; " +
                    "${context.getString(R.string.activityDetails_AverageDescription)}; " +
                    "${context.getString(R.string.activityDetails_InputReport)}",
            Charsets.ISO_8859_1
        )
        shareFile.appendText("\n")

        // Content
        shareFile.appendText(
            "${rifleName}; " +
                    "${training.indicator}; " +
                    "${training.training}; " +
                    "${training.place}; " +
                    "${SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(training.date)}; " +
                    "${training.shootCount}; " +
                    "${training.shoots[0]}; " +
                    "${training.shoots[1]}; " +
                    "${training.shoots[2]}; " +
                    "${training.shoots[3]}; " +
                    "${training.shoots[4]}; " +
                    "${training.shoots[5]}; " +
                    "${training.shoots[6]}; " +
                    "${training.shoots[7]}; " +
                    "${training.shoots[8]}; " +
                    "${training.shoots.sum()}; " +
                    "${(training.shoots.sum() / training.shootCount * 100) / 100.0}; " +
                    "${training.report}",
            Charsets.ISO_8859_1
        )

        return shareFile
    }

    fun shareCompetition(
        competition: EntryCompetition,
        rifleName: String?,
        sharePath: File?,
        context: Context
    ): File {

        // Create the file
        val shareFile = createFile(sharePath)

        // Header
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputWeapon)}; " +
                    "${context.getString(R.string.activityDetails_CompetitionInputKind)}; " +
                    "${context.getString(R.string.activityDetails_CompetitionInputPlace)}; " +
                    "${context.getString(R.string.activityDetails_InputDate)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints1)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints2)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints3)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints4)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints5)}; " +
                    "${context.getString(R.string.activityDetails_InputPoints6)}; " +
                    "${context.getString(R.string.activityDetails_Points)}; " +
                    "${context.getString(R.string.activityDetails_CompetitionInputReport)}",
            Charsets.ISO_8859_1
        )
        shareFile.appendText("\n")

        // Content
        shareFile.appendText(
            "${rifleName}; " +
                    "${competition.kind}; " +
                    "${competition.place}; " +
                    "${SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(competition.date)}; " +
                    "${competition.shoots[0]}; " +
                    "${competition.shoots[1]}; " +
                    "${competition.shoots[2]}; " +
                    "${competition.shoots[3]}; " +
                    "${competition.shoots[4]}; " +
                    "${competition.shoots[5]}; " +
                    "${competition.shoots.sum()}; " +
                    "${competition.report}",
            Charsets.ISO_8859_1
        )
        shareFile.appendText("\n")

        return shareFile
    }

    private fun createFile(sharePath: File?): File {
        // Create the file
        val shareFile = File(sharePath, "share.csv")
        shareFile.delete()
        shareFile.createNewFile()

        // Return the file
        return shareFile
    }
}