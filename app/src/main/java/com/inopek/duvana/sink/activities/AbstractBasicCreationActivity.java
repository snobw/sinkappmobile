package com.inopek.duvana.sink.activities;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.hasText;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.photoExists;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.setDefaultClient;

public abstract class AbstractBasicCreationActivity extends AbstractCreationActivity {


    protected abstract void populateFromExtras(String extra);

    @Override
    protected void customInitialize() {
        setContentView(R.layout.activity_basic_sink_creation);
        addCameraButtonListener((Button) findViewById(R.id.cameraPreviousButton));
        addSaveSendLaterButtonListener((Button) findViewById(R.id.saveButton));
        String extra = getIntent().getStringExtra("sinkBean");
        if (extra != null) {
            populateFromExtras(extra);
        }
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

        setClient(sinkBean);

        if(StringUtils.isNotEmpty(sinkBean.getImageAfter())) {
            Bitmap bipMapFromFile = ImageUtils.getBipMapFromFile(sinkBean.getImageAfter());
            if(bipMapFromFile != null) {
                sinkBean.setImageAfter(customService.encodeBase64(bipMapFromFile));
            }
        }

        boolean photoExists = photoExists(sinkBean.getImageBefore(), (Button) findViewById(R.id.cameraPreviousButton));

        return referenceExist && photoExists;
    }

    @Override
    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.imageViewBefore);
    }

    private void setClient(SinkBean sinkBean) {
        setDefaultClient(sinkBean, this, getString(R.string.client_name_preference));
    }
}