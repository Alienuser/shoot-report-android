package de.famprobst.report.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterTabs
import de.famprobst.report.fragment.data.FragmentDevice
import de.famprobst.report.fragment.data.FragmentPerson
import de.famprobst.report.helper.HelperRepeat

class ActivityMasterData : AppCompatActivity() {

    private var prevMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define layout
        setContentView(R.layout.activity_masterdata)

        // Define toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Define viewPager and bottomNav
        val viewPager: ViewPager = findViewById(R.id.activityData_viewPager)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.activityData_bottomNav)
        setupViewPager(viewPager, bottomNavigation)
        setupBottomNav(bottomNavigation, viewPager)

        // Add back button to activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title
        supportActionBar?.title = getString(R.string.activityData_Title)
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

    private fun setupViewPager(viewPager: ViewPager, bottomNav: BottomNavigationView) {
        val adapter = AdapterTabs(supportFragmentManager)
        adapter.addFragment(FragmentPerson())
        adapter.addFragment(FragmentDevice())
        viewPager.adapter = adapter

        // Set menu marker if swipe
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
                R.id.menu_person -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.menu_device -> {
                    viewPager.currentItem = 1
                    true
                }
                else -> false
            }
        }
    }
}