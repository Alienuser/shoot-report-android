package de.famprobst.report.helper

import android.content.Context
import de.famprobst.report.R
import de.famprobst.report.model.ModelCompetition
import de.famprobst.report.model.ModelTraining
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object HelperExport {

    private val charset = Charsets.ISO_8859_1

    fun exportTraining(
        modelTraining: ModelTraining,
        rifleName: String?,
        rifleId: Int,
        sharePath: File?,
        context: Context
    ): File {

        // Create the file
        val shareFile = this.createFile(sharePath)

        // Header
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputWeapon)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputIndicator)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputKind)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputPlace)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputDate)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputCount)};",
            this.charset
        )
        for (i in 1..this.getMaxSeriesTraining()) {
            shareFile.appendText(
                "${
                    String.format(
                        context.getString(R.string.activityDetails_InputPoints),
                        i
                    )
                };", this.charset
            )
        }
        shareFile.appendText("${context.getString(R.string.activityDetails_Points)};", this.charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_AverageDescription)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputReport)};",
            this.charset
        )
        shareFile.appendText("\n")

        // Content
        modelTraining.allTrainings(rifleId).observeForever { trainings ->
            trainings.forEach { training ->
                shareFile.appendText("${rifleName};", this.charset)
                shareFile.appendText("${training.indicator};", this.charset)
                shareFile.appendText("${training.training};", this.charset)
                shareFile.appendText("${training.place};", this.charset)
                shareFile.appendText(
                    "${
                        SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.GERMAN
                        ).format(training.date)
                    };", this.charset
                )
                shareFile.appendText("${training.shootCount};", this.charset)
                for (i in 0..training.shoots.size) {
                    try {
                        shareFile.appendText("${training.shoots[i]};", this.charset)
                    } catch (e: IndexOutOfBoundsException) {
                        shareFile.appendText("0;", this.charset)
                    }
                }
                shareFile.appendText("${training.shoots.sum()};", this.charset)
                shareFile.appendText(
                    "%.2f;".format(
                        (training.shoots.sum() / training.shootCount * 100) / 100.0
                    ), this.charset
                )
                shareFile.appendText("${training.report};", this.charset)
                shareFile.appendText("\n")
            }
        }

        return shareFile
    }

    fun exportCompetition(
        modelCompetition: ModelCompetition,
        rifleName: String?,
        rifleId: Int,
        sharePath: File?,
        context: Context
    ): File {

        // Create the file
        val shareFile = this.createFile(sharePath)

        // Header
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputWeapon)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputKind)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputPlace)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputDate)};",
            this.charset
        )
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputCount)};",
            this.charset
        )
        for (i in 1..this.getMaxSeriesCompetition()) {
            shareFile.appendText(
                "${
                    String.format(
                        context.getString(R.string.activityDetails_InputPoints),
                        i
                    )
                };", this.charset
            )
        }
        shareFile.appendText("${context.getString(R.string.activityDetails_Points)};", this.charset)
        shareFile.appendText(
            "${context.getString(R.string.activityDetails_InputReport)};",
            this.charset
        )
        shareFile.appendText("\n")

        // Content
        modelCompetition.allCompetitions(rifleId).observeForever { competitions ->
            competitions.forEach { competition ->
                shareFile.appendText("${rifleName};", this.charset)
                shareFile.appendText("${competition.kind};", this.charset)
                shareFile.appendText("${competition.place};", this.charset)
                shareFile.appendText(
                    "${
                        SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.GERMAN
                        ).format(competition.date)
                    };", this.charset
                )
                shareFile.appendText("${competition.shootCount};", this.charset)
                for (i in 0..competition.shoots.size) {
                    try {
                        shareFile.appendText("${competition.shoots[i]};", this.charset)
                    } catch (e: IndexOutOfBoundsException) {
                        shareFile.appendText("0;", this.charset)
                    }
                }
                shareFile.appendText("${competition.shoots.sum()};", this.charset)
                shareFile.appendText("${competition.report};", this.charset)
                shareFile.appendText("\n")
            }
        }

        return shareFile
    }

    private fun getMaxSeriesTraining(): Int {
        return 10
    }

    private fun getMaxSeriesCompetition(): Int {
        return 10
    }

    private fun createFile(sharePath: File?): File {
        // Create the file
        val shareFile = File(sharePath, "export.csv")
        shareFile.delete()
        shareFile.createNewFile()

        // Return the file
        return shareFile
    }
}