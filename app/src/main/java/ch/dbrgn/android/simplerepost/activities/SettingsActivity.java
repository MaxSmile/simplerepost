/**
 * SimpleRepost -- A simple Instagram reposting Android app.
 * Copyright (C) 2014-2015 Danilo Bargen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package ch.dbrgn.android.simplerepost.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ch.dbrgn.android.simplerepost.R;


public class SettingsActivity extends PreferenceActivity {

    // Log tag
    public static final String LOG_TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Manually add action bar (see http://stackoverflow.com/a/27455363/284318)
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        LinearLayout content = (LinearLayout) root.getChildAt(0);
        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.activity_settings, null);
        root.removeAllViews();
        toolbarContainer.addView(content);
        root.addView(toolbarContainer);

        // Set navigation onClickListener
        final Toolbar toolbar = (Toolbar) toolbarContainer.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load fragments
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final GeneralPreferenceFragment fragment = new GeneralPreferenceFragment();
        fragmentTransaction.add(R.id.contentFragment, fragment);
        fragmentTransaction.commit();
    }



    public static class GeneralPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        // Private members
        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            mSharedPreferences = getPreferenceScreen().getSharedPreferences();

            // Register onSharedPreferenceChangeListener
            mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

            // Trigger manually for initial display.
            // TODO: This is a hack, change event key should not be hardcoded
            onSharedPreferenceChanged(mSharedPreferences, "captionTextPreference");
        }

        @Override
        public void onPause() {
            super.onPause();

            // Unregister onSharedPreferenceChangeListener
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(LOG_TAG, "onSharedPreferenceChanged");
            Preference pref = findPreference(key);

            if (pref instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                String text = editTextPref.getText();
                pref.setSummary(getExcerpt(text, 100));
                Log.i(LOG_TAG, "Updating EditTextPreference summary");
            }
        }

        /**
         * Get an excerpt of a string. If the string has been shortened, append "...".
         */
        private String getExcerpt(String text, int length) {
            if (length < text.length()) {
                return text.substring(0, length) + "...";
            } else {
                return text;
            }
        }

    }

}