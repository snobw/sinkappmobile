package com.inopek.duvana.sink.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.utils.ImageUtils;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;
import static com.inopek.duvana.sink.services.CustomServiceUtils.hasText;
import static com.inopek.duvana.sink.services.CustomServiceUtils.photoExists;

public class SinkBeforeCreationActivity extends AppCompatActivity {

    private ImageView imageViewBefore;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_before_sink_creation);
        addCameraButtonListener();
        addSaveSendLaterButtonListener();
    }

    private void addCameraButtonListener() {
        Button cameraButtonBefore = (Button) findViewById(R.id.cameraPreviousButton);
        imageViewBefore = (ImageView) findViewById(R.id.imageViewBefore);

        cameraButtonBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openChoice = new Intent(getBaseContext(), PhotoChoiceActivity.class);
                startActivityForResult(openChoice, PHOTO_REQUEST_CODE);
            }
        });
    }

    private void addSaveSendLaterButtonListener() {
        Button saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinkBean sinkBean = new SinkBean();
                Context context = getBaseContext();
                if (createSinkBean(sinkBean)) {
                    setClient(sinkBean);
                    boolean fileCreated = customService.createAndSaveFile(sinkBean, getBaseContext());
                    if (fileCreated) {
                        ActivityUtils.showToastMessage(getString(R.string.success_save_message), context);
                        initialize();
                    } else {
                        ActivityUtils.showToastMessage(getString(R.string.try_later_message), context);
                    }
                } else {
                    // Message d'error
                    ActivityUtils.showToastMessage(getString(R.string.required_fields_empty_message), context);
                }
            }
        });
    }

    private boolean createSinkBean(SinkBean sinkBean) {
        EditText referenceText = (EditText) findViewById(R.id.referenceTxt);
        boolean referenceExist = hasText(referenceText, "");

        Bitmap imageViewBeforeDrawingCache = imageViewBefore.getDrawingCache();
        if (imageViewBeforeDrawingCache != null) {
            sinkBean.setImageBefore(customService.encodeBase64(imageViewBeforeDrawingCache));
        }

        sinkBean.setReference(referenceText.getText().toString());
        sinkBean.setSinkCreationDate(new Date());

        TextView textView = (TextView) findViewById(R.id.imageBeforeTextView);
        textView.setVisibility(View.INVISIBLE);

        boolean photoExists = photoExists(sinkBean.getImageBefore(), textView);

        return referenceExist && photoExists;

    }

    private void setClient(SinkBean sinkBean) {
        try {
            String clientName = PropertiesUtils.getProperty("duvana.sink.client", getApplicationContext());
            ClientBean clientBean = new ClientBean(clientName);
            sinkBean.setClient(clientBean);
        } catch (IOException e) {
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable() {
            public void run() {
                final Bitmap bitmap = ImageUtils.processBitMap(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        if (bitmap != null) {
                            imageViewBefore.setImageBitmap(bitmap);
                            imageViewBefore.setDrawingCacheEnabled(true);
                            TextView textView = (TextView) findViewById(R.id.imageBeforeTextView);
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

        }).start();
    }


}
