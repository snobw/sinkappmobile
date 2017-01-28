package com.inopek.duvana.sink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.inopek.duvana.sink.activities.SinkCreationActivity;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.RequirementValidationService;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    RequirementValidationService requirementValidationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        checkCamera();

        // open sink creation activity
        createSinkActivity();

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
        requirementValidationService.checkCameraHardware(getBaseContext());
    }

    private void createSinkIntent() {
        Intent openSinkCreationIntent = new Intent(this, SinkCreationActivity.class);
        startActivity(openSinkCreationIntent);
    }
}
