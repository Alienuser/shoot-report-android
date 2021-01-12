package de.famprobst.report.fragment.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterTabs

class FragmentGoals : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_goals, container, false)

        // Setup tab layout
        setupTabLayout(layout)

        return layout
    }

    private fun setupTabLayout(layout: View) {

        // Set layout und viewer
        val tabLayout = layout.findViewById<TabLayout>(R.id.fragmentGoals_Tabs)
        val viewPager = layout.findViewById<ViewPager>(R.id.fragmentGoals_viewPager)

        tabLayout!!.addTab(tabLayout.newTab().setText(R.string.fragmentGoal_TabComplete))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragmentGoal_TabTenth))

        // Set adapter
        val adapter = AdapterTabs(childFragmentManager)
        adapter.addFragment(FragmentGoalsComplete())
        adapter.addFragment(FragmentGoalsTenth())
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