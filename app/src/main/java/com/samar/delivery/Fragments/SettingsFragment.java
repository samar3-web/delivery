package com.samar.delivery.Fragments;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.samar.delivery.R;

public class SettingsFragment extends Fragment {

    private Spinner themeSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialiser et configurer votre UI ici
        themeSpinner = view.findViewById(R.id.themeSpinner);

        // Charger la préférence actuelle du thème
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String currentTheme = preferences.getString("theme_preference", "");

        // Configurer le Spinner avec les options de thème
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.theme_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);

        // Sélectionner l'option de thème actuelle
        int position = adapter.getPosition(getThemeLabel(currentTheme));
        Log.d("pospospospospos",""+getThemeLabel(currentTheme)+" "+currentTheme);
        Log.d("pospospospospos",""+position);
        themeSpinner.setSelection(position);


        // Écouter les changements de sélection du Spinner
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Enregistrer la nouvelle préférence de thème
                String selectedTheme = getThemeValue(position);
                if(!currentTheme.equals(selectedTheme)){
                updateTheme(selectedTheme);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("settings_fragment", "true");
                editor.apply();
                }
                else
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("settings_fragment", "false");
                    editor.apply();
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire ici

            }
        });


        return view;
    }

    private void updateTheme(String theme) {
        // Enregistrer la préférence dans SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("theme_preference", theme);
        editor.apply();
        if ("system".equals(theme)) {
           // getActivity().setTheme(R.style.Theme_Delivery);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if ("light".equals(theme)) {
            //getActivity().setTheme(R.style.Theme_Delivery);
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        } else if ("dark".equals(theme)) {
           // getActivity().setTheme(R.style.Theme_Delivery_Dark);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }



        // Appliquer le nouveau thème à l'activité
        // Assurez-vous que votre activité prend en charge le changement de thème
        // (par exemple, en utilisant AppCompatDelegate.setDefaultNightMode)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        // ou
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        // ou
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private String getThemeLabel(String themeValue) {
        switch (themeValue) {
            case "system":
                return "system";
            case "light":
                return "light";
            case "dark":
                return "dark";
            default:
                return "system";
        }
    }

    private String getThemeValue(int position) {
        switch (position) {
            case 0:
                return "system";
            case 1:
                return "light";
            case 2:
                return "dark";
            default:
                return "system";
        }
    }
}
