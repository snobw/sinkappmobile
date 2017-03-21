package com.inopek.duvana.sink.activities;

import android.graphics.Bitmap;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.adapters.SpinnerArrayAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbOptionEnum;
import com.inopek.duvana.sink.enums.SinkStatusEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;
import com.inopek.duvana.sink.services.CustomServiceUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;


public class SinkEditionActivity extends AbstractInputActivity {

    private Long id;
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
            id = sinkBean.getId();
            populate(sinkBean);
        }
    }

    @Override
    protected void updateCustomValues(SinkBean sinkBean) {
        sinkBean.setSinkUpdateDate(new Date());
    }

    private void populate(SinkBean sinkBean) {
        getReferenceEditText().setText(sinkBean.getReference());
        getAdresseeEditText().setText(sinkBean.getAddress() != null ? sinkBean.getAddress().getStreet() : StringUtils.EMPTY);
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

    private void populatePhotoAfter(SinkBean sinkBean) {
        if(sinkBean.getImageAfter() != null) {
            Bitmap bitmap = CustomServiceUtils.decodeBase64(sinkBean.getImageAfter());
            getImageView().setImageBitmap(bitmap);
            getImageView().setDrawingCacheEnabled(true);
        }
    }

}
