package com.inopek.duvana.sink.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    private Spinner profileSpinner;
    private Spinner clientSpinner;
    private List<String> profileOptions;
    private List<String> clientOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initProfileSpinner();
        initClientSpinner();
        addSaveListener();
    }

    private void initProfileSpinner() {
        profileSpinner = (Spinner) findViewById(R.id.profileSpinner);
        profileOptions = Arrays.asList(ProfileEnum.BEGIN.getLabel(), ProfileEnum.END.getLabel());
        ActivityUtils.mapSpinner(profileSpinner, profileOptions, getBaseContext());
        initSelectedProfilePreference();
    }

    private void initClientSpinner() {
        try {
            clientSpinner = (Spinner) findViewById(R.id.clientSpinner);
            String clientProperty = PropertiesUtils.getProperty("duvana.default.client.list", getBaseContext());
            String[] split = StringUtils.split(clientProperty, ",");
            clientOptions = Arrays.asList(split);
            ActivityUtils.mapSpinner(clientSpinner, clientOptions, getBaseContext());
            initSelectedClientPreference();
        } catch (IOException e) {
            Log.e(SettingsActivity.class.getSimpleName(), "Error while loading clients list");
        }

    }

    private void addSaveListener() {
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingPreferences();
            }
        });
    }

    private void settingPreferences() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.client_name_preference), (String) clientSpinner.getSelectedItem());
        editor.putString(getString(R.string.profile_name_preference), (String) profileSpinner.getSelectedItem());
        editor.commit();
        Intent openMain = new Intent(this, MainActivity.class);
        startActivity(openMain);
    }

    private void initSelectedProfilePreference() {
        String profilePreference = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        profileSpinner.setSelection(profileOptions.indexOf(profilePreference));
    }

    private void initSelectedClientPreference() {
        String clientPreference = ActivityUtils.getStringPreference(this, R.string.client_name_preference, getString(R.string.client_name_preference));
        clientSpinner.setSelection(clientOptions.indexOf(clientPreference));
    }
}
