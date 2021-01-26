package de.famprobst.report.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterTabs
import de.famprobst.report.fragment.competition.FragmentCompetition
import de.famprobst.report.fragment.goals.FragmentGoals
import de.famprobst.report.fragment.procedure.FragmentProcedure
import de.famprobst.report.fragment.training.FragmentTraining
import de.famprobst.report.helper.HelperRepeat
import de.famprobst.report.model.ModelCompetition
import de.famprobst.report.model.ModelTraining


class ActivityMain : AppCompatActivity() {

    private var prevMenuItem: MenuItem? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_main)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Get shared prefs
        sharedPref = this.getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Define viewPager and bottomNav
        val viewPager: ViewPager = findViewById(R.id.activityMain_viewPager)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.activityMain_bottomNav)
        setupViewPager(viewPager, bottomNavigation)
        setupBottomNav(bottomNavigation, viewPager)

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title and subtitle
        supportActionBar?.title =
            sharedPref.getString(getString(R.string.preferenceReportRifleName), "")
    }

    override fun onResume() {
        super.onResume()

        // Start changing ad
        HelperRepeat.startRepeat(window.decorView.rootView, baseContext)
    }

    override fun onPause() {
        super.onPause()

        // Stop changing ad
        HelperRepeat.stopRepeat()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.topMenuData -> {
                startActivity(Intent(this, ActivityMasterData::class.java))
                true
            }
            R.id.topMenuTrainer -> {
                startActivity(Intent(this, ActivityTrainer::class.java))
                true
            }
            R.id.topMenuExport -> {
                exportDataToCSV()
                true
            }
            R.id.topMenuInfo -> {
                startActivity(Intent(this, ActivityInformation::class.java))
                true
            }
            R.id.topMenuDonate -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.paypal.com/paypalme/shootreport/5")
                    )
                )
                true
            }
            R.id.topMenuPartner -> {
                startActivity(Intent(this, ActivityPartner::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewPager(viewPager: ViewPager, bottomNav: BottomNavigationView) {
        val adapter = AdapterTabs(supportFragmentManager)
        adapter.addFragment(FragmentTraining())
        adapter.addFragment(FragmentCompetition())
        adapter.addFragment(FragmentProcedure())
        adapter.addFragment(FragmentGoals())
        viewPager.adapter = adapter

        // Set menu marker if swipe
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    bottomNav.menu.getItem(0).isChecked = false
                }
                bottomNav.menu.getItem(position).isChecked = true
                prevMenuItem = bottomNav.menu.getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })
    }

    private fun setupBottomNav(bottomNav: BottomNavigationView, viewPager: ViewPager) {
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_training -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.menu_competition -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.menu_plan -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.menu_goals -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }

    private fun exportDataToCSV() {
        val trainingModel = ViewModelProvider(this).get(ModelTraining::class.java)
        val competitionModel = ViewModelProvider(this).get(ModelCompetition::class.java)

        trainingModel.allTrainings(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(this, { trainings ->
            println(trainings)
        })

        competitionModel.allCompetitions(
            sharedPref.getInt(
                getString(R.string.preferenceReportRifleId),
                0
            )
        ).observe(this, { competitions ->
            println(competitions)
        })
    }
}