package com.example.yousafkhan.equake;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthQuakePreferenceFragment extends PreferenceFragmentCompat
                                               implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preference);

            // get preference
            Preference preference = findPreference(getString(R.string.settings_min_magnitude_key));

            //set preference change listener
            preference.setOnPreferenceChangeListener(this);

            // show preference value in preference summary when the settings activity is launched
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceValue = sharedPrefs.getString(preference.getKey(), "");
            preference.setSummary(preferenceValue);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String preferenceValue = newValue.toString();
            preference.setSummary(preferenceValue);
            return true;
        }

    }

}
