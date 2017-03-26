package com.inopek.duvana.sink.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSendBeanTask;
import com.inopek.duvana.sink.utils.ImageUtils;

import javax.inject.Inject;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;
import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;

public abstract class AbstractCreationActivity extends AppCompatActivity {

    private Bitmap imageBitmap;

    @Inject
    CustomService customService;

    protected abstract void customInitialize();

    protected abstract boolean createSinkBean(SinkBean sinkBean);

    protected abstract ImageView getImageView();

    protected abstract SinkBean getSinkBeanToSave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        initialize();
    }

    private void initialize() {
        customInitialize();
    }

    protected void addSaveSendLaterButtonListener(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinkBean sinkBean = getSinkBeanToSave();
                Context context = getBaseContext();
                if (createSinkBean(sinkBean)) {
                    boolean fileCreated = customService.createAndSaveFile(sinkBean, getBaseContext());
                    if (fileCreated) {
                        setResultActivity();
                        finish();
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

    protected void setResultActivity() {
        ActivityUtils.showToastMessage(getString(R.string.success_save_message), getBaseContext());
        if (getParent() == null) {
            setResult(SinkConstants.EDITION_ACTIVITY_RESULT_CODE, new Intent());
        } else {
            getParent().setResult(SinkConstants.EDITION_ACTIVITY_RESULT_CODE, new Intent());
        }
    }

    protected void addCameraButtonListener(Button cameraButton) {

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openChoice = new Intent(getBaseContext(), PhotoChoiceActivity.class);
                startActivityForResult(openChoice, PHOTO_REQUEST_CODE);
            }
        });
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
                            getImageView().setImageBitmap(bitmap);
                            getImageView().setDrawingCacheEnabled(true);
                            imageBitmap = bitmap;
                        }
                    }
                });
            }

        }).start();
    }

    protected void runTask(final SinkBean sink, final boolean checkReferenceExists, final boolean updateAll) {

        new HttpRequestSendBeanTask(sink, getBaseContext(), ActivityUtils.getCurrentUser(this), checkReferenceExists, updateAll) {

            ProgressDialog dialog;

            @Override
            protected void onPostExecute(Long id) {
                dialog.dismiss();

                if (id == null) {
                    showToastMessage(getString(R.string.try_later_message), getBaseContext());
                } else if (id == 0L) {
                    createAlertReferenceDialog(sink);
                } else {
                    setResultActivity();
                    finish();
                }
            }

            @Override
            protected void onPreExecute() {
                dialog = createDialog();
            }
        }.execute();

    }

    private void createAlertReferenceDialog(final SinkBean sinkBean) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // send again
                        runTask(sinkBean, false, true);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.reference_exists_save_message))
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private ProgressDialog createDialog() {
        return ActivityUtils.createProgressDialog(getString(R.string.sending_default_message), this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("BitmapImage", imageBitmap);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Parcelable parcelable = savedInstanceState.getParcelable("BitmapImage");
        if (parcelable != null) {
            Bitmap bitmapImage = (Bitmap) parcelable;
            getImageView().setImageBitmap(bitmapImage);
            getImageView().setDrawingCacheEnabled(true);
        }
    }
}
