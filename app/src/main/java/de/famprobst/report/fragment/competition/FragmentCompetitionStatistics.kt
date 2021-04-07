package de.famprobst.report.fragment.competition

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
import de.famprobst.report.model.ModelCompetition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class FragmentCompetitionStatistics : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var competitionModel: ModelCompetition
    private lateinit var chartViewHalf: AAChartView
    private lateinit var chartViewComplete: AAChartView
    private var chartSeriesComplete: Array<Any> = emptyArray()
    private var chartSeriesHalf: Array<Any> = emptyArray()
    private var chartCategoriesComplete: Array<String> = emptyArray()
    private var chartCategoriesHalf: Array<String> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Return the layout
        val view = inflater.inflate(R.layout.fragment_competition_statistics, container, false)

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        chartViewComplete = view.findViewById(R.id.fragmentCompetition_StatisticsComplete)
        chartViewHalf = view.findViewById(R.id.fragmentCompetition_StatisticsHalf)

        // Get the data
        getData()

        // Return the layout
        return view
    }

    private fun getData() {

        competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)
        competitionModel.allCompetitions(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(viewLifecycleOwner, { competitions ->
            // Empty the arrays
            chartSeriesComplete = emptyArray()
            chartSeriesHalf = emptyArray()
            chartCategoriesComplete = emptyArray()
            chartCategoriesHalf = emptyArray()

            // Fill the complete array
            var i = 0

            while (chartCategoriesComplete.size < 10 && competitions.isNotEmpty() && i < competitions.size) {
                if (competitions[i].shoots.isNotEmpty() && competitions[i].shoots.all { it % 1.0 == 0.0 }) {
                    // Calculate the right points
                    chartSeriesComplete += competitions[i].shoots.sum()

                    // Add data to categories
                    chartCategoriesComplete += SimpleDateFormat(
                        "dd. MMM",
                        Locale.getDefault()
                    ).format(competitions[i].date)
                }

                i++
            }

            // Fill the half array
            i = 0

            while (chartCategoriesHalf.size < 10 && competitions.isNotEmpty() && i < competitions.size) {
                if (competitions[i].shoots.isNotEmpty() && competitions[i].shoots.any { it % 1.0 != 0.0 }) {
                    // Calculate the right points
                    chartSeriesHalf += floor(competitions[i].shoots.sum() * 100) / 100.0

                    // Add data to categories
                    chartCategoriesHalf += SimpleDateFormat(
                        "dd. MMM",
                        Locale.getDefault()
                    ).format(competitions[i].date)
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
            .title(getString(R.string.fragmentCompetitionStatistics_TitleComplete))
            .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold))
            .zoomType(AAChartZoomType.XY)
            .backgroundColor("#FAFAFA")
            .yAxisTitle(getString(R.string.fragmentCompetitionStatistics_Points))
            .colorsTheme(arrayOf("#0AE20A"))
            .categories(chartCategoriesComplete.reversedArray())
            .series(
                arrayOf(
                    AASeriesElement()
                        .showInLegend(false)
                        .name(getString(R.string.fragmentCompetitionStatistics_Result))
                        .data(chartSeriesComplete.reversedArray())
                )
            )

        val aaChartModelHalf = AAChartModel()
            .chartType(AAChartType.Spline)
            .title(getString(R.string.fragmentCompetitionStatistics_TitleHalf))
            .titleStyle(AAStyle().fontWeight(AAChartFontWeightType.Bold))
            .zoomType(AAChartZoomType.XY)
            .backgroundColor("#FAFAFA")
            .yAxisTitle(getString(R.string.fragmentCompetitionStatistics_Points))
            .colorsTheme(arrayOf("#0E2435"))
            .categories(chartCategoriesHalf.reversedArray())
            .series(
                arrayOf(
                    AASeriesElement()
                        .showInLegend(false)
                        .name(getString(R.string.fragmentCompetitionStatistics_Result))
                        .data(chartSeriesHalf.reversedArray())
                )
            )

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        chartViewComplete.aa_drawChartWithChartModel(aaChartModelComplete)
        chartViewHalf.aa_drawChartWithChartModel(aaChartModelHalf)
    }
}