package com.inopek.duvana.sink.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        checkCamera();

        // open sink creation activity
        createSettingkActivity();
        createSendSinkActivity();
        chekPermissions();
        checkPreferences();
        settingPreferences();
        checkProfileAndCreateActivities();
        endActivity();
    }

    private void checkPreferences() {
        String profilePreference = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        String clientPreference = ActivityUtils.getStringPreference(this, R.string.client_name_preference, getString(R.string.client_name_preference));

        if (StringUtils.isEmpty(profilePreference) || StringUtils.isEmpty(clientPreference)) {
            finish();
            startActivity(getSettingActivityIntent());
        }
    }

    private void settingPreferences() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        editor.putString(getString(R.string.imi_name_preference), telephonyManager.getDeviceId());
        editor.commit();
    }

    private void checkProfileAndCreateActivities() {
        String profilePreference = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        if (ProfileEnum.BEGIN.getLabel().equals(profilePreference)) {
            beforeCreateSinkActivity();
            Button button = (Button) findViewById(R.id.addSinkButton);
            button.setEnabled(false);
        } else if (ProfileEnum.END.getLabel().equals(profilePreference)) {
            createSinkActivity();
            Button button = (Button) findViewById(R.id.addSinkBeforeButton);
            button.setEnabled(false);
        }
    }

    private void chekPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE}, PHOTO_REQUEST_CODE);

    }

    private void createSendSinkActivity() {
        Button sendSinkButton = (Button) findViewById(R.id.sendSinkButton);
        sendSinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSinkIntent();
            }
        });
    }

    private void beforeCreateSinkActivity() {
        Button beforeCreateSinkButton = (Button) findViewById(R.id.addSinkBeforeButton);
        beforeCreateSinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeCreateSinkIntent();
            }
        });
    }

    private void createSinkActivity() {
        Button createSinkButton = (Button) findViewById(R.id.addSinkButton);
        createSinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSinkIntent();
            }
        });
    }

    private void createSettingkActivity() {
        Button configButton = (Button) findViewById(R.id.configButton);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingIntent();
            }
        });
    }

    private void checkCamera() {
        customService.checkCameraHardware(getBaseContext());
    }

    private void beforeCreateSinkIntent() {
        Intent openSinkBeforeCreationIntent = new Intent(this, SinkBeforeCreationActivity.class);
        startActivity(openSinkBeforeCreationIntent);
    }

    private void createSinkIntent() {
        Intent openSinkCreationIntent = new Intent(this, SinkCreationActivity.class);
        startActivity(openSinkCreationIntent);
    }

    private void sendSinkIntent() {
        Intent openSinkSendIntent = new Intent(this, SinkSendActivity.class);
        startActivity(openSinkSendIntent);
    }

    private void endActivity() {
        Button closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void settingIntent() {
        Intent settingIntent = getSettingActivityIntent();
        startActivity(settingIntent);
    }

    private Intent getSettingActivityIntent() {
        return new Intent(this, SettingsActivity.class);
    }
}
