package com.inopek.duvana.sink.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import java.io.IOException;

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
        sendSinkActivity();
        chekPermissions();
        settingPreferences();
        checkProfileAndCreateActivities();
        endActivity();
    }

    private void settingPreferences() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            editor.putString(getString(R.string.client_name_preference), PropertiesUtils.getProperty("duvana.default.client", getBaseContext()));
            editor.putString(getString(R.string.profile_name_preference), PropertiesUtils.getProperty("duvana.default.profile", getBaseContext()));
            editor.putString(getString(R.string.imi_name_preference), telephonyManager.getDeviceId());
            editor.commit();
        } catch (IOException e) {
            Log.e("Setting preferences ", e.getCause().getMessage());
        }
    }

    private void checkProfileAndCreateActivities() {
        String profilePreference = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        if (ProfileEnum.BEGIN.name().equals(profilePreference)) {
            beforeCreateSinkActivity();
            Button button = (Button) findViewById(R.id.addSinkButton);
            button.setEnabled(false);
        } else if(ProfileEnum.END.name().equals(profilePreference)) {
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

    private void sendSinkActivity() {
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
}
