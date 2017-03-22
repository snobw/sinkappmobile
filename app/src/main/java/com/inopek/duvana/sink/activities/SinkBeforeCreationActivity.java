package com.inopek.duvana.sink.activities;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;

import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.hasText;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.photoExists;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.setDefaultClient;

public class SinkBeforeCreationActivity extends AbstractCreationActivity {


    @Override
    protected void customInitialize() {
        setContentView(R.layout.activity_before_sink_creation);
        addCameraButtonListener((Button) findViewById(R.id.cameraPreviousButton));
        addSaveSendLaterButtonListener((Button) findViewById(R.id.saveButton));
    }

    @Override
    protected boolean createSinkBean(SinkBean sinkBean) {

        EditText referenceText = (EditText) findViewById(R.id.referenceTxt);
        boolean referenceExist = hasText(referenceText, (EditText) findViewById(R.id.referenceTxtTitle));

        Bitmap imageViewBeforeDrawingCache = getImageView().getDrawingCache();
        if (imageViewBeforeDrawingCache != null) {
            sinkBean.setImageBefore(customService.encodeBase64(imageViewBeforeDrawingCache));
        }

        sinkBean.setReference(referenceText.getText().toString());
        sinkBean.setSinkCreationDate(new Date());

        TextView textView = (TextView) findViewById(R.id.imageBeforeTextView);
        textView.setVisibility(View.INVISIBLE);

        setClient(sinkBean);

        boolean photoExists = photoExists(sinkBean.getImageBefore(), textView);

        return referenceExist && photoExists;
    }

    @Override
    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.imageViewBefore);
    }

    @Override
    protected TextView getTextViewImage() {
        return (TextView) findViewById(R.id.imageBeforeTextView);
    }

    @Override
    protected SinkBean getSinkBeanToSave() {
        return new SinkBean();
    }

    private void setClient(SinkBean sinkBean) {
        setDefaultClient(sinkBean, this, getString(R.string.client_name_preference));
    }
}
