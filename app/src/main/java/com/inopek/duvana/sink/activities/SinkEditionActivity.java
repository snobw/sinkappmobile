package com.inopek.duvana.sink.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.adapters.SpinnerArrayAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbOptionEnum;
import com.inopek.duvana.sink.enums.SinkStatusEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;


public class SinkEditionActivity extends AbstractInputActivity {

    private SinkBean sinkBean;

    @Override
    protected void addSendListenerAction() {
        if (createSinkBean(sinkBean)) {
            runTask(sinkBean, false, true);
        }
    }

    @Override
    protected void populateFromExtras(String extra) {
        Gson gson = new GsonBuilder().create();
        sinkBean = gson.fromJson(extra, SinkBean.class);
        if (sinkBean != null) {
            populate(sinkBean);
        }
        if (sinkBean.getFileName() != null) {
            File file = new File(sinkBean.getFileName());
            if (file.isFile()) {
                View sendButton = findViewById(R.id.sendButton);
                sendButton.setVisibility(View.GONE);
            } else {
                hideSaveButton();
            }
        } else {
            hideSaveButton();
        }
        getReferenceEditText().setEnabled(false);
    }

    private void hideSaveButton() {
        View saveButton = findViewById(R.id.saveAndSendLaterButton);
        saveButton.setVisibility(View.GONE);
    }

    @Override
    protected void updateCustomValues(SinkBean sinkBean) {
        sinkBean.setSinkUpdateDate(new Date());
    }

    private void populate(SinkBean sinkBean) {
        getReferenceEditText().setText(sinkBean.getReference());
        getAddressEditText().setText(sinkBean.getAddress() != null ? sinkBean.getAddress().getStreet() : StringUtils.EMPTY);
        getNeighborhoodEditText().setText(sinkBean.getAddress() != null ? sinkBean.getAddress().getNeighborhood() : StringUtils.EMPTY);
        getLengthEditText().setText(sinkBean.getLength() != null ? String.valueOf(sinkBean.getLength()) : StringUtils.EMPTY);
        getPipelineLengthEditText().setText(sinkBean.getPipeLineLength() != null ? String.valueOf(sinkBean.getPipeLineLength()) : StringUtils.EMPTY);
        getObservationsEditText().setText(sinkBean.getObservations());
        populateStatus(sinkBean);
        populateType(sinkBean);
        populateDiameter(sinkBean);
        populatePlumOptions(sinkBean);
        populatePhotoAfter(sinkBean);
    }

    private void populateStatus(SinkBean sinkBean) {
        Spinner statusSpinner = getStatusSpinner();
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) statusSpinner.getAdapter();
        Integer positionForItem = adapter.getPositionForItem(SinkStatusEnum.getSinkStatusEnumById(sinkBean.getSinkStatusId()).getLabel());
        statusSpinner.setSelection(positionForItem);
    }

    private void populateType(SinkBean sinkBean) {
        Spinner typeSpinner = getTypeSpinner();
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) typeSpinner.getAdapter();
        Integer positionForItem = adapter.getPositionForItem(SinkTypeEnum.getSinkTypeEnumById(sinkBean.getSinkTypeId()).getLabel());
        typeSpinner.setSelection(positionForItem);
    }

    private void populateDiameter(SinkBean sinkBean) {
        Spinner diameterSpinner = getDiameterSpinner();
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) diameterSpinner.getAdapter();
        Integer positionForItem = adapter.getPositionForItem(SinkDiameterEnum.getSinkDiameterEnumById(sinkBean.getPipeLineDiameterId()).getLabel());
        diameterSpinner.setSelection(positionForItem);
    }

    private void populatePlumOptions(SinkBean sinkBean) {
        Spinner plumbSpinner = getPlumbSpinner();
        SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) plumbSpinner.getAdapter();
        Integer positionForItem = adapter.getPositionForItem(SinkPlumbOptionEnum.getSinkPlumbEnumById(sinkBean.getPlumbOptionId()).getLabel());
        plumbSpinner.setSelection(positionForItem);
    }

    private void populatePhotoAfter(final SinkBean sinkBean) {
        if (sinkBean.getImageAfter() != null) {
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            // search file
                            File imageFile = new File(sinkBean.getImageAfter());
                            if (imageFile.exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                                getImageView().setImageBitmap(bitmap);
                                getImageView().setDrawingCacheEnabled(true);
                            }
                        }
                    });
                }

            }).start();
        }
    }

    @Override
    protected SinkBean getSinkBeanToSave() {
        return sinkBean;
    }
}
