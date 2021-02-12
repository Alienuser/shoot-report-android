package de.famprobst.report.fragment.procedure

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import de.famprobst.report.R
import de.famprobst.report.model.ModelRifle

class FragmentProcedureBefore : PreferenceFragmentCompat() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        // Get shared prefs
        sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preferenceFile_report),
            Context.MODE_PRIVATE
        )

        // Get model and load rifle entity
        val model = ViewModelProvider(this).get(ModelRifle::class.java)
        model.getRifle(sharedPref.getInt(getString(R.string.preferenceReportRifleId), 0))
            .observe(this, {

                // Set the preference name
                preferenceManager.sharedPreferencesName = it.prefFile

                // Set the preference layout
                setPreferencesFromResource(R.xml.preferences_plan_before, rootKey)
            })
    }
}