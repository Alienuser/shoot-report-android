package de.famprobst.report.helper

import android.content.Context
import de.famprobst.report.R
import de.famprobst.report.entity.EntryCompetition
import de.famprobst.report.entity.EntryTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object HelperExport {

    fun getTraining(
        context: Context,
        shareFile: File,
        training: List<EntryTraining>?,
        rifleName: String?
    ): File {

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
        training?.forEach {
            shareFile.appendText(
                "${rifleName}; " +
                        "${it.indicator}; " +
                        "${it.training}; " +
                        "${it.place}; " +
                        "${SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(it.date)}; " +
                        "${it.shootCount}; " +
                        "${it.shoots[0]}; " +
                        "${it.shoots[1]}; " +
                        "${it.shoots[2]}; " +
                        "${it.shoots[3]}; " +
                        "${it.shoots[4]}; " +
                        "${it.shoots[5]}; " +
                        "${it.shoots[6]}; " +
                        "${it.shoots[7]}; " +
                        "${it.shoots[8]}; " +
                        "${it.shoots.sum()}; " +
                        "${(it.shoots.sum() / it.shootCount * 100) / 100.0}; " +
                        "${it.report}",
                Charsets.ISO_8859_1
            )
            shareFile.appendText("\n")
        }

        return shareFile
    }

    fun getCompetition(
        context: Context,
        shareFile: File,
        competition: List<EntryCompetition>?,
        rifleName: String?
    ): File {

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
        competition?.forEach {
            shareFile.appendText(
                "${rifleName}; " +
                        "${it.kind}; " +
                        "${it.place}; " +
                        "${SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(it.date)}; " +
                        "${it.shoots[0]}; " +
                        "${it.shoots[1]}; " +
                        "${it.shoots[2]}; " +
                        "${it.shoots[3]}; " +
                        "${it.shoots[4]}; " +
                        "${it.shoots[5]}; " +
                        "${it.shoots.sum()}; " +
                        "${it.report}",
                Charsets.ISO_8859_1
            )
            shareFile.appendText("\n")
        }

        return shareFile
    }
}