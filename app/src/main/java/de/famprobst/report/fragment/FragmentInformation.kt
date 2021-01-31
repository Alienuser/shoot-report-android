package de.famprobst.report.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.famprobst.report.R

class FragmentInformation : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Set the preference layout
        setPreferencesFromResource(R.xml.preferences_info, rootKey)
    }
}