package com.example.android.guardiannews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class ArticlePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //find the preference to be updated
            Preference articlesLimit = findPreference(getString(R.string.settings_list_items_limit_key));
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            Preference selectedCategories = findPreference(getString(R.string.settings_select_category_key));

            // bind the current preference value to be displayed.
            bindPreferenceSummaryToValue(articlesLimit);
            bindPreferenceSummaryToValue(orderBy);
            bindPreferenceSummaryToValue(selectedCategories);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            //set listener to listen to changes of the preference we pass in
            preference.setOnPreferenceChangeListener(this);

            //Read the current value of the preference stored in the SharedPreferences on the device,
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            //Display the value in the preference summary
            Object preferenceString;
            if (preference instanceof MultiSelectListPreference) {
                preferenceString = preferences.getStringSet(preference.getKey(), new HashSet<String>());
            } else {
                preferenceString = preferences.getString(preference.getKey(), "");
            }
            onPreferenceChange(preference, preferenceString);
        }
    }
}