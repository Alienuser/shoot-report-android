package de.famprobst.report.fragment.data

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.famprobst.report.R

class FragmentDevice : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        // Set the preference layout
        setPreferencesFromResource(R.xml.preferences_data_device, rootKey)
    }
}