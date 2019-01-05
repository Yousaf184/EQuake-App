package com.example.yousafkhan.equake;

import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class EarthQuakePreferenceFragment extends PreferenceFragmentCompat
                                               implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preference);

            // get preference
            Preference minMagPref = findPreference(getString(R.string.settings_min_magnitude_key));
            Preference orderByPref = findPreference(getString(R.string.settings_order_by_key));

            //set preference change listener
            minMagPref.setOnPreferenceChangeListener(this);
            orderByPref.setOnPreferenceChangeListener(this);

            // show preference value in preference summary when the settings activity is launched
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(minMagPref.getContext());

            String preferenceValue = sharedPrefs.getString(minMagPref.getKey(), "");
            minMagPref.setSummary(preferenceValue);

            String prefValue = sharedPrefs.getString(orderByPref.getKey(), "");
            orderByPref.setSummary(prefValue);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String preferenceValue = newValue.toString();
            preference.setSummary(preferenceValue);
            return true;
        }

    }

}
