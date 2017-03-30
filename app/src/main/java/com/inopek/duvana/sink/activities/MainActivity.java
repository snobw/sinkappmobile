package com.inopek.duvana.sink.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;

import javax.inject.Inject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 10;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        checkCamera();
        askForPermission();
        endActivity();
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE};
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        } else {
            // Permission Granted
            createSettingsActivity();
            settingPreferences();
            createSendSinkActivity();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (hasAllPermissionsGranted()) {
                    // Permission Granted
                    createSettingsActivity();
                    settingPreferences();
                    createSendSinkActivity();

                } else {
                    // Permission Denied
                    askForPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean hasAllPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void settingPreferences() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String identifier = telephonyManager.getDeviceId();
        if(identifier == null) {
            identifier = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        editor.putString(getString(R.string.imei_name_preference), identifier);
        editor.commit();
        checkProfileAndCreateActivities();

    }

    private void checkProfileAndCreateActivities() {
        String profilePreference = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        if (ProfileEnum.BEGIN.getLabel().equals(profilePreference)) {
            beforeCreateSinkActivity();
            createSearchActivity();
            Button button = (Button) findViewById(R.id.addSinkButton);
            button.setEnabled(false);
        } else if (ProfileEnum.END.getLabel().equals(profilePreference)) {
            createSinkActivity();
            createSearchActivity();
            Button button = (Button) findViewById(R.id.addSinkBeforeButton);
            button.setEnabled(false);
        } else {
            finish();
            startActivity(getSettingActivityIntent());
        }
    }


    private void createSearchActivity() {
        Button configButton = (Button) findViewById(R.id.searchSinkButton);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIntent();
            }
        });
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

    private void createSettingsActivity() {
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
        Intent openSinkBeforeCreationIntent = new Intent(this, SinkBasicCreationActivity.class);
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

    private void searchIntent() {
        startActivity(new Intent(this, SinkSearchActivity.class));
    }

    private Intent getSettingActivityIntent() {
        return new Intent(this, SettingsActivity.class);
    }
}
