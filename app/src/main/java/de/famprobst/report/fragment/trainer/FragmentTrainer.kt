package de.famprobst.report.fragment.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.famprobst.report.R
import de.famprobst.report.adapter.AdapterTabs

class FragmentTrainer(private val tabNumber: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_trainer, container, false)

        // Setup tab layout
        setupTabLayout(layout)

        // Return the layout
        return layout
    }

    private fun setupTabLayout(layout: View) {

        // Set layout und viewer
        val tabLayout = layout.findViewById<TabLayout>(R.id.fragmentTrainer_Tabs)
        val viewPager = layout.findViewById<ViewPager>(R.id.fragmentTrainer_viewPager)
        val adapter = AdapterTabs(childFragmentManager)

        when (tabNumber) {
            1 -> {
                // Tab Equipment
                tabLayout!!.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerEquipment_TabCloth)
                )
                tabLayout.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerEquipment_TabSport)
                )
                tabLayout.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerEquipment_TabEquipment)
                )

                adapter.addFragment(FragmentTrainerTab(1))
                adapter.addFragment(FragmentTrainerTab(2))
                adapter.addFragment(FragmentTrainerTab(3))
                viewPager!!.adapter = adapter
            }
            2 -> {
                // Tab Tech
                tabLayout!!.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerTech_TabAttack)
                )
                tabLayout.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerTech_TabDestination)
                )

                adapter.addFragment(FragmentTrainerTab(4))
                adapter.addFragment(FragmentTrainerTab(5))
                viewPager!!.adapter = adapter
            }
            3 -> {
                // Tab Mental
                tabLayout!!.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerMental_TabRelax)
                )
                tabLayout.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerMental_TabMotivation)
                )
                tabLayout.addTab(
                    tabLayout.newTab().setText(R.string.fragmentTrainerMental_TabFocus)
                )

                adapter.addFragment(FragmentTrainerTab(6))
                adapter.addFragment(FragmentTrainerTab(7))
                adapter.addFragment(FragmentTrainerTab(8))
                viewPager!!.adapter = adapter
            }
        }

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