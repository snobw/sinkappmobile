package com.inopek.duvana.sink.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.utils.PathUtils;

import static com.inopek.duvana.sink.constants.SinkConstants.INTENT_EXTRA_FILE_NAME;
import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;

public class PhotoChoiceActivity extends AppCompatActivity implements OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int SELECT_REQUEST_CODE = 1;

    private Button cameraButton;
    private Button galleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_choice);

        cameraButton = (Button) findViewById(R.id.cameraButton);
        galleryButton = (Button) findViewById(R.id.galeryButton);

        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cameraButton:
                // Launch camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                break;

            case R.id.galeryButton:
                // Open gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Intent intent = new Intent();
            String realPathFromURI = null;
            if (requestCode == CAMERA_REQUEST_CODE && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = PathUtils.getImageUri(getApplicationContext(), photo);
                realPathFromURI = PathUtils.generatePath(tempUri, getApplicationContext());
            } else if (requestCode == SELECT_REQUEST_CODE) {
                realPathFromURI = PathUtils.generatePath(data.getData(), getApplicationContext());
            }
            intent.putExtra(INTENT_EXTRA_FILE_NAME, realPathFromURI);
            setResult(PHOTO_REQUEST_CODE, intent);
            finish();
        }
    }
}