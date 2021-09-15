package de.famprobst.report.helper

import android.content.Context
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.entity.EntryTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object HelperShare {

    private val charset = Charsets.ISO_8859_1

    fun shareTraining(
        training: EntryTraining,
        rifleName: String?,
        sharePath: File?,
        context: Context
    ): File {

        // Create the file
        val shareFile = createFile(sharePath)

        // Header
        shareFile.appendText("${context.getString(R.string.activityDetails_InputWeapon)};", charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputIndicator)};",
            charset
        )
        shareFile.appendText("${context.getString(R.string.activityDetails_InputKind)};", charset)
        shareFile.appendText("${context.getString(R.string.activityDetails_InputPlace)};", charset)
        shareFile.appendText("${context.getString(R.string.activityDetails_InputDate)};", charset)
        shareFile.appendText("${context.getString(R.string.activityDetails_InputCount)};", charset)
        for (i in 1..training.shoots.size) {
            shareFile.appendText(
                "${
                    String.format(
                        context.getString(R.string.activityDetails_InputPoints),
                        i
                    )
                };", charset
            )
        }
        shareFile.appendText("${context.getString(R.string.activityDetails_Points)};", charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_AverageDescription)};",
            charset
        )
        shareFile.appendText("${context.getString(R.string.activityDetails_InputReport)};", charset)
        shareFile.appendText("\n")

        // Content
        shareFile.appendText("${rifleName};", charset)
        shareFile.appendText("${training.indicator};", charset)
        shareFile.appendText("${training.training};", charset)
        shareFile.appendText("${training.place};", charset)
        shareFile.appendText(
            "${
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.GERMAN
                ).format(training.date)
            };", charset
        )
        shareFile.appendText("${training.shootCount};", charset)
        training.shoots.forEach { shoot ->
            shareFile.appendText("${shoot};", charset)
        }
        shareFile.appendText("${training.shoots.sum()};", charset)
        shareFile.appendText(
            "%.2f;".format(
                (training.shoots.sum() / training.shootCount * 100) / 100.0
            ), this.charset
        )
        shareFile.appendText("${training.report};", charset)
        shareFile.appendText("\n")

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
        shareFile.appendText("${context.getString(R.string.activityDetails_InputWeapon)};", charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_CompetitionInputKind)};",
            charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_CompetitionInputPlace)};",
            charset
        )
        shareFile.appendText("${context.getString(R.string.activityDetails_InputDate)};", charset)
        shareFile.appendText("${context.getString(R.string.activityDetails_InputCount)};", charset)
        for (i in 1..competition.shoots.size) {
            shareFile.appendText(
                "${
                    String.format(
                        context.getString(R.string.activityDetails_InputPoints),
                        i
                    )
                };", charset
            )
        }
        shareFile.appendText("${context.getString(R.string.activityDetails_Points)};", this.charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_CompetitionInputReport)};",
            charset
        )
        shareFile.appendText("\n")

        // Content
        shareFile.appendText("${rifleName};", charset)
        shareFile.appendText("${competition.kind};", charset)
        shareFile.appendText("${competition.place};", charset)
        shareFile.appendText(
            "${
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.GERMAN
                ).format(competition.date)
            };", charset
        )
        shareFile.appendText("${competition.shoots.sum()};", charset)
        competition.shoots.forEach { shoot ->
            shareFile.appendText("${shoot};", charset)
        }
        shareFile.appendText("${competition.shoots.sum()};", charset)
        shareFile.appendText("${competition.report};", charset)
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