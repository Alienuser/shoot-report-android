package de.famprobst.report.fragment.training

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import de.famprobst.report.R
import de.famprobst.report.model.ModelTraining
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class FragmentTrainingStatistics : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var trainingModel: ModelTraining
    private lateinit var chartViewComplete: AAChartView
    private lateinit var chartViewHalf: AAChartView
    private var chartSeriesComplete: Array<Any> = emptyArray()
    private var chartSeriesHalf: Array<Any> = emptyArray()
    private var chartCategoriesComplete: Array<String> = emptyArray()
    private var chartCategoriesHalf: Array<String> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_training_statistics, container, false)

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        chartViewComplete = view.findViewById(R.id.fragmentTraining_StatisticsComplete)
        chartViewHalf = view.findViewById(R.id.fragmentTraining_StatisticsHalf)

        // Get the data
        getData()

        // Return the layout
        return view
    }

    private fun getData() {

        trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        trainingModel.allTrainings(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(viewLifecycleOwner, { trainings ->
            // Empty the arrays
            chartSeriesComplete = emptyArray()
            chartSeriesHalf = emptyArray()
            chartCategoriesComplete = emptyArray()
            chartCategoriesHalf = emptyArray()

            // Fill the complete array
            var i = 0

            while (chartCategoriesComplete.size < 10 && trainings.isNotEmpty() && i < trainings.size) {
                if (trainings[i].shoots.sum().rem(1).equals(0.0)) {
                    // Calculate the right points
                    if (trainings[i].shootCount == 0) {
                        chartSeriesComplete += 0
                    } else {
                        chartSeriesComplete += floor(trainings[i].shoots.sum() / trainings[i].shootCount * 100) / 100
                    }

                    // Add data to categories
                    chartCategoriesComplete += SimpleDateFormat(
                        "dd. MMM",
                        Locale.getDefault()
                    ).format(trainings[i].date)
                }

                i++
            }

            // Fill the half array
            i = 0

            while (chartCategoriesHalf.size < 10 && trainings.isNotEmpty() && i < trainings.size) {
                if (!trainings[i].shoots.sum().rem(1).equals(0.0)) {
                    // Check if we divide by 0
                    if (trainings[i].shootCount == 0) {
                        chartSeriesHalf += 0
                        break
                    }

                    // Calculate the right points
                    chartSeriesHalf += floor((trainings[i].shoots.sum() / trainings[i].shootCount) * 100) / 100.0

                    // Add data to categories
                    chartCategoriesHalf += SimpleDateFormat(
                        "dd. MMM",
                        Locale.getDefault()
                    ).format(
                        trainings[i].date
                    )
                }

                i++
            }

            // Set the chart
            setupChart()
        })
    }

    private fun setupChart() {
        val aaChartModelComplete = AAChartModel()
            .chartType(AAChartType.Spline)
            .title(getString(R.string.fragmentTrainingStatistics_TitleComplete))
            .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold))
            .zoomType(AAChartZoomType.XY)
            .backgroundColor("#FAFAFA")
            .yAxisTitle(getString(R.string.fragmentTrainingStatistics_Points))
            .colorsTheme(arrayOf("#0AE20A"))
            .categories(chartCategoriesComplete.reversedArray())
            .series(
                arrayOf(
                    AASeriesElement()
                        .showInLegend(false)
                        .name("\\u00D8")
                        .data(chartSeriesComplete.reversedArray())
                )
            )

        val aaChartModelHalf = AAChartModel()
            .chartType(AAChartType.Spline)
            .title(getString(R.string.fragmentTrainingStatistics_TitleHalf))
            .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold))
            .zoomType(AAChartZoomType.XY)
            .backgroundColor("#FAFAFA")
            .yAxisTitle(getString(R.string.fragmentTrainingStatistics_Points))
            .colorsTheme(arrayOf("#0E2435"))
            .categories(chartCategoriesHalf.reversedArray())
            .series(
                arrayOf(
                    AASeriesElement()
                        .showInLegend(false)
                        .name("\\u00D8")
                        .data(chartSeriesHalf.reversedArray())
                )
            )

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        chartViewComplete.aa_drawChartWithChartModel(aaChartModelComplete)
        chartViewHalf.aa_drawChartWithChartModel(aaChartModelHalf)
    }
}