package de.famprobst.report.fragment.training

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterTabs

class FragmentTraining : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_training, container, false)

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Setup tab layout
        setupTabLayout(layout)

        // Return the layout
        return layout
    }

    private fun setupTabLayout(layout: View) {

        // Set layout und viewer
        val tabLayout = layout.findViewById<TabLayout>(R.id.fragmentTraining_Tabs)
        val viewPager = layout.findViewById<ViewPager>(R.id.fragmentTraining_viewPager)

        tabLayout!!.addTab(tabLayout.newTab().setText(R.string.fragmentTraining_TabTraining))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragmentTraining_TabStatistics))

        // Set adapter
        val adapter = AdapterTabs(childFragmentManager)
        adapter.addFragment(FragmentTrainingList())
        adapter.addFragment(FragmentTrainingStatistics())
        viewPager!!.adapter = adapter

        // Set pageChangeListener
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}