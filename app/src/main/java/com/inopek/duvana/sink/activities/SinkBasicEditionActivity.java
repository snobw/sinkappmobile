package com.inopek.duvana.sink.activities;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;


public class SinkBasicEditionActivity extends AbstractBasicCreationActivity {

    private SinkBean sinkBean;

    protected void populateFromExtras(String extra) {
        Button sendButton = (Button) findViewById(R.id.sendButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        sendButton.setText(getString(R.string.button_send_changes_title));
        saveButton.setText(getString(R.string.save_changes_default_message));
        addSendListenerAction(sendButton);

        createObjectAndPopulate(extra, sendButton, saveButton);
    }

    private void createObjectAndPopulate(String extra, Button sendButton, Button saveButton) {
        Gson gson = new GsonBuilder().create();
        sinkBean = gson.fromJson(extra, SinkBean.class);
        if (sinkBean != null) {
            populate(sinkBean);
        }
        if (sinkBean.getFileName() != null) {
            File file = new File(sinkBean.getFileName());
            if (file.isFile()) {
                sendButton.setVisibility(View.GONE);
            } else {
                activateSendComponents(sendButton, saveButton);
            }
        } else {
            activateSendComponents(sendButton, saveButton);
        }
    }

    private void addSendListenerAction(Button sendButton) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getBaseContext();
                if(!ActivityUtils.isNetworkAvailable(context)) {
                    showToastMessage(getString(R.string.no_network_available_message), context);
                } else if (createSinkBean(sinkBean)) {
                    runTask(sinkBean, false, true);
                } else {
                    // Message d'error
                    showToastMessage(getString(R.string.required_fields_empty_message), context);
                }
            }
        });
    }

    private void activateSendComponents(Button sendButton, Button saveButton) {
        saveButton.setVisibility(View.GONE);
        getReferenceEditText().setEnabled(false);
        sendButton.setVisibility(View.VISIBLE);
    }

    private void populate(SinkBean sinkBean) {
        getReferenceEditText().setText(sinkBean.getReference());
        populatePhoto(sinkBean);
    }

    private void populatePhoto(final SinkBean sinkBean) {
        if (StringUtils.isNotEmpty(sinkBean.getImageBefore())) {
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ImageUtils.createBipMapFromFile(sinkBean.getImageBefore(), getImageView());
                        }
                    });
                }

            }).start();
        }
    }

    @Override
    protected SinkBean getSinkBeanToSave() {
        sinkBean.setSinkUpdateDate(new Date());
        return sinkBean;
    }

    @Override
    protected boolean isModeEdition() {
        return true;
    }

}
