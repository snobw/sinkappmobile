package com.inopek.duvana.sink.activities;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SpinnerArrayAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbOptionEnum;
import com.inopek.duvana.sink.enums.SinkStatusEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;
import com.inopek.duvana.sink.utils.AddressUtils;
import com.inopek.duvana.sink.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;


public class SinkEditionActivity extends AbstractInputActivity {

    private SinkBean sinkBean;

    @Override
    protected void addSendListenerAction() {
        Context context = getBaseContext();
        if (!ActivityUtils.isNetworkAvailable(context)) {
            showToastMessage(getString(R.string.no_network_available_message), context);
        } else if (createSinkBean(sinkBean)) {
            runTask(sinkBean, false, true);
        } else {
            // Message d'error
            ActivityUtils.showToastMessage(getString(R.string.required_fields_empty_message), context);
        }
    }

    @Override
    protected void populateFromExtras(String extra) {

        Button sendButton = (Button) findViewById(R.id.sendButton);
        Button saveButton = (Button) findViewById(R.id.saveAndSendLaterButton);
        sendButton.setText(getString(R.string.button_send_changes_title));
        saveButton.setText(getString(R.string.save_changes_default_message));

        Gson gson = new GsonBuilder().create();
        sinkBean = gson.fromJson(extra, SinkBean.class);
        addressBean = sinkBean.getAddress();
        if (sinkBean != null) {
            populate(sinkBean);
        }
        if (sinkBean.getFileName() != null) {
            File file = new File(sinkBean.getFileName());
            if (file.isFile()) {
                sendButton.setVisibility(View.GONE);
            } else {
                hideSaveButtonAndDisableReference();
            }
        } else {
            hideSaveButtonAndDisableReference();
        }
    }

    private void hideSaveButtonAndDisableReference() {
        View saveButton = findViewById(R.id.saveAndSendLaterButton);
        getReferenceEditText().setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }

    @Override
    protected void updateCustomValues(SinkBean sinkBean) {
        sinkBean.setSinkUpdateDate(new Date());
    }

    @Override
    protected void getAddressFromLocation() {
        addressBean = sinkBean.getAddress() != null ? sinkBean.getAddress() : AddressUtils.initAddressFromLocation((EditText) findViewById(R.id.addressTxt), (EditText) findViewById(R.id.neighborhoodTxt), getBaseContext(), lastLocation);
    }

    @Override
    protected SinkBean getSinkBeanToSave() {
        return sinkBean;
    }

    @Override
    protected boolean isModeEdition() {
        return true;
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
        if (sinkBean.getSinkStatusId() != null) {
            Spinner statusSpinner = getStatusSpinner();
            SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) statusSpinner.getAdapter();
            Integer positionForItem = adapter.getPositionForItem(SinkStatusEnum.getSinkStatusEnumById(sinkBean.getSinkStatusId()).getLabel());
            statusSpinner.setSelection(positionForItem);
        }
    }

    private void populateType(SinkBean sinkBean) {
        if (sinkBean.getSinkTypeId()!= null) {
            Spinner typeSpinner = getTypeSpinner();
            SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) typeSpinner.getAdapter();
            Integer positionForItem = adapter.getPositionForItem(SinkTypeEnum.getSinkTypeEnumById(sinkBean.getSinkTypeId()).getLabel());
            typeSpinner.setSelection(positionForItem);
        }
    }

    private void populateDiameter(SinkBean sinkBean) {
        if (sinkBean.getPipeLineDiameterId() != null) {
            Spinner diameterSpinner = getDiameterSpinner();
            SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) diameterSpinner.getAdapter();
            Integer positionForItem = adapter.getPositionForItem(SinkDiameterEnum.getSinkDiameterEnumById(sinkBean.getPipeLineDiameterId()).getLabel());
            diameterSpinner.setSelection(positionForItem);
        }
    }

    private void populatePlumOptions(SinkBean sinkBean) {
        if (sinkBean.getPlumbOptionId() != null) {
            Spinner plumbSpinner = getPlumbSpinner();
            SpinnerArrayAdapter adapter = (SpinnerArrayAdapter) plumbSpinner.getAdapter();
            Integer positionForItem = adapter.getPositionForItem(SinkPlumbOptionEnum.getSinkPlumbEnumById(sinkBean.getPlumbOptionId()).getLabel());
            plumbSpinner.setSelection(positionForItem);
        }
    }

    private void populatePhotoAfter(final SinkBean sinkBean) {
        if (StringUtils.isNotEmpty(sinkBean.getImageAfter())) {
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ImageUtils.createBipMapFromFile(sinkBean.getImageAfter(), getImageView());
                        }
                    });
                }

            }).start();
        }
    }

}
