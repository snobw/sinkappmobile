package com.inopek.duvana.sink.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;

import javax.inject.Inject;

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
        createSinkActivity();
        sendSinkActivity();
        beforeCreateSinkActivity();
        chekPermissions();

    }

    private void chekPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_REQUEST_CODE);

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
}
