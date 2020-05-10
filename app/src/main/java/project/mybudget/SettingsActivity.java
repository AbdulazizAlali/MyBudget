package project.mybudget;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;


import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import project.mybudget.Authentication.Login;


public class SettingsActivity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);

        return rootView;

    }

    public static class BudgetPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

//            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
//            bindPreferenceSummaryToValue(minMagnitude);
//
//            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
//            bindPreferenceSummaryToValue(orderBy);
//
//            Preference maxMagnitude = findPreference(getString(R.string.settings_max_magnitude_key));
//            bindPreferenceSummaryToValue(maxMagnitude);
//
//            Preference listItems = findPreference(getString(R.string.settings_list_items_key));
//            bindPreferenceSummaryToValue(listItems);
//            bindPreferenceSummaryToValue(listItems);

            Preference signOut = findPreference("sign_out");
            signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), Login.class));
                    getActivity().finish();
                    return false;
                }
            });

            Preference language = findPreference("lang");
            bindPreferenceSummaryToValue(language);

            findPreference("lang").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String loadLanguage =  (String) newValue;
                    Locale locale = null;
                    if (loadLanguage.equals("English")) {
                        locale = new Locale("en");

                    } else if (loadLanguage.equals("ku")) {
                        locale = new Locale("ku");

                    } else if(loadLanguage.equals("العربية")){
                        locale = new Locale("ar");

                    }else if(loadLanguage.equals("tr")){
                        locale = new Locale("tr");
                    }
                    else if(loadLanguage.equals("fr")){
                        locale = new Locale("fr");
                    }
                    SharedPreferences preferences = StartupActivity.context.getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE);

                    SharedPreferences.Editor eneditor = preferences.edit();
                    eneditor.putString("lang", locale.getLanguage());
                    eneditor.apply();
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    StartupActivity.context.getResources().updateConfiguration(config,
                            StartupActivity.context.getResources().getDisplayMetrics());
                    startActivity(new Intent( MainActivity.context, MainActivity.class));
                    getActivity().finish();
                    return true;
                }
            });
//            Preference c = findPreference("sign_out");
//            signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    startActivity(new Intent(getContext(), Login.class));
//                    getActivity().finish();
//                    return false;
//                }
//            });


        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
//            String stringValue = value.toString();
//            if (preference instanceof ListPreference) {
//                ListPreference listPreference = (ListPreference) preference;
//                int prefIndex = listPreference.findIndexOfValue(stringValue);
//
//                CharSequence[] labels = listPreference.getEntries();
//                preference.setSummary(stringValue);
//                SharedPreferences preferences = Login.context.getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE);
//
//                if (value != preference.getSharedPreferences().getString("lang", null)) {
//                    String Language = "";
//                    if (value.equals("ar")) {
//                        if (!preference.getSharedPreferences().getString("lang", null).equals("ar")) {
//                            Language = "ar";
//                            preferences.edit().putString("lang", "ar").apply();
//                            setLocale("ar", getContext());
//                            Intent refresh = new Intent(getActivity(), MainActivity.class);
//                            startActivity(refresh);
//                            getActivity().finish();
//                        } else return true;
//                    } else {
//                        if (!preference.getSharedPreferences().getString("lang", null).equals("English")) {
//                            Language = "en";
//                            preferences.edit().putString("lang", "en").apply();
//                            setLocale("en", getContext());
//                            Intent refresh = new Intent(getActivity(), MainActivity.class);
//                            startActivity(refresh);
//                            getActivity().finish();
//                        } else return true;
//                    }
//
//                }
//            }
            return true;
        }




        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
//            onPreferenceChange(preference, preferenceString);

        }
    }
}

