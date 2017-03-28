package com.inopek.duvana.sink.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.AddressBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbOptionEnum;
import com.inopek.duvana.sink.enums.SinkStatusEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;
import com.inopek.duvana.sink.utils.AddressUtils;
import com.inopek.duvana.sink.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.hasText;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initDiameterSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initPlumbSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initStateSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initTypeSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.isNumeric;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.isValidSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.photoExists;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.setDefaultClient;

public abstract class AbstractInputActivity extends AbstractCreationActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected Location lastLocation;
    protected AddressBean addressBean;

    protected abstract void addSendListenerAction();

    protected abstract void populateFromExtras(String extra);

    protected abstract void updateCustomValues(SinkBean sinkBean);

    @Override
    protected void customInitialize() {
        setContentView(R.layout.activity_sink_creation);
        addCameraButtonListener((Button) findViewById(R.id.cameraFinalButton));
        addSendButtonListener();
        addSaveSendLaterButtonListener((Button) findViewById(R.id.saveAndSendLaterButton));
        Context context = getBaseContext();
        initTypeSpinner(getTypeSpinner(), context);
        initStateSpinner(getStatusSpinner(), context);
        initDiameterSpinner(getDiameterSpinner(), context);
        initPlumbSpinner(getPlumbSpinner(), context);
        initLocationRequest();
        addSpinnerTypeListener();
        String extra = getIntent().getStringExtra("sinkBean");
        if (extra != null) {
            populateFromExtras(extra);
        }
    }

    private void addSpinnerTypeListener() {
        getTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String typeSelected = getTypeSpinner().getSelectedItem().toString();
                getLengthEditText().setEnabled(!SinkTypeEnum.COVENTIONAL.getLabel().equals(typeSelected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected boolean createSinkBean(SinkBean sinkBean) {

        sinkBean.setAddress(getAddressBean());
        updateCustomValues(sinkBean);

        Bitmap imageViewAfterDrawingCache = getImageView().getDrawingCache();

        if (imageViewAfterDrawingCache != null) {
            sinkBean.setImageAfter(customService.encodeBase64(imageViewAfterDrawingCache));
        }
        getImageView().setDrawingCacheEnabled(true);
        setDefaultClient(sinkBean, this, getString(R.string.client_name_preference));

        if(StringUtils.isNotEmpty(sinkBean.getImageBefore())) {
            Bitmap bipMapFromFile = ImageUtils.getBipMapFromFile(sinkBean.getImageBefore());
            if(bipMapFromFile != null) {
                sinkBean.setImageBefore(customService.encodeBase64(bipMapFromFile));
            }
        }

        boolean referenceExists = referenceExists(sinkBean);
        boolean validSinkStatusEnum = isValidSinkStatusEnum(sinkBean);
        boolean validSinkTypeEnum = isValidSinkTypeEnum(sinkBean);
        boolean validSinkDiameterEnum = isValidSinkDiameterEnum(sinkBean);
        boolean validSinkPlumbEnum = isValidSinkPlumbEnum(sinkBean);
        boolean photoExists = isPhotoTaken(sinkBean);

        return referenceExists && validSinkStatusEnum && validSinkTypeEnum && validSinkDiameterEnum && validSinkPlumbEnum && photoExists;
    }

    private boolean isPhotoTaken(SinkBean sinkBean) {
        Button button = (Button) findViewById(R.id.cameraFinalButton);
        return photoExists(sinkBean.getImageAfter(), button);
    }

    private boolean isValidSinkStatusEnum(SinkBean sinkBean) {
        Spinner spinner = getStatusSpinner();
        TextView textView = (TextView) findViewById(R.id.stateTitle);
        if (isValidSpinner(spinner, textView)) {
            String selectedItem = (String) spinner.getSelectedItem();
            sinkBean.setSinkStatusId(SinkStatusEnum.getSinkStatutEnumByName(selectedItem).getId());
            return true;
        }
        return false;
    }

    private boolean isValidSinkTypeEnum(SinkBean sinkBean) {

        Spinner spinner = getTypeSpinner();
        if (isValidSpinner(getTypeSpinner(), (TextView) findViewById(R.id.typeTitle))) {
            String selectedItem = (String) spinner.getSelectedItem();
            SinkTypeEnum sinkTypeEnum = SinkTypeEnum.getSinkTypeEnum(selectedItem);
            if (Arrays.asList(SinkTypeEnum.LATERAL, SinkTypeEnum.TRANSVERSAL).contains(sinkTypeEnum)) {
                EditText lengthText = getLengthEditText();
                boolean lengthExists = isNumeric(lengthText, (EditText) findViewById(R.id.lengthTitle));
                if (lengthExists) {
                    sinkBean.setLength(Long.valueOf(lengthText.getText().toString().trim()));
                } else {
                    return false;
                }
            }
            sinkBean.setSinkTypeId(sinkTypeEnum.getId());
            return true;
        }
        return false;
    }

    private boolean isValidSinkDiameterEnum(SinkBean sinkBean) {
        Spinner spinner = getDiameterSpinner();
        if (isValidSpinner(spinner, (TextView) findViewById(R.id.diameterTitle))) {
            String selectedItem = (String) spinner.getSelectedItem();
            sinkBean.setPipeLineDiameterId(SinkDiameterEnum.getSinkDiameterEnum(selectedItem).getId());
            return true;
        }
        return false;
    }

    private boolean isValidSinkPlumbEnum(SinkBean sinkBean) {
        Spinner spinner = getPlumbSpinner();
        if (isValidSpinner(spinner, (TextView) findViewById(R.id.plumbOptionTitle))) {
            String selectedItem = (String) spinner.getSelectedItem();
            SinkPlumbOptionEnum sinkPlumbOptionEnum = SinkPlumbOptionEnum.getSinkPlumbEnum(selectedItem);
            if (SinkPlumbOptionEnum.YES.equals(sinkPlumbOptionEnum)) {
                EditText pipeLineLengthText = getPipelineLengthEditText();
                boolean lengthExists = isNumeric(pipeLineLengthText, (EditText) findViewById(R.id.pipelineLengthTitle));
                if (lengthExists) {
                    sinkBean.setPipeLineLength(Long.valueOf(pipeLineLengthText.getText().toString().trim()));
                } else {
                    return false;
                }
            }
            sinkBean.setPlumbOptionId(sinkPlumbOptionEnum.getId());
            return true;
        }
        return false;
    }

    private boolean referenceExists(SinkBean sinkBean) {

        boolean referenceExist = hasText(getReferenceEditText(), (EditText) findViewById(R.id.referenceTitle));

        if (referenceExist) {
            sinkBean.setReference(getReferenceEditText().getText().toString());
            sinkBean.setObservations(getObservationsEditText().getText().toString());
        }
        return referenceExist;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation != null) {
            getAddressFromLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        ActivityUtils.showToastMessage(getString(R.string.address_gps_error_message), getBaseContext());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ActivityUtils.showToastMessage(getString(R.string.address_gps_error_message), getBaseContext());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        if (location != null) {
            lastLocation = location;
            if (addressBean == null) {
                addressBean = AddressUtils.initAddressFromLocation((EditText) findViewById(R.id.addressTxt), (EditText) findViewById(R.id.neighborhoodTxt), getBaseContext(), lastLocation);
            }
        }
    }

    @Override
    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.imageViewAfter);
    }

    protected EditText getAddressEditText() {
        return (EditText) findViewById(R.id.addressTxt);
    }

    protected EditText getNeighborhoodEditText() {
        return (EditText) findViewById(R.id.neighborhoodTxt);
    }

    protected EditText getLengthEditText() {
        return (EditText) findViewById(R.id.lengthTxt);
    }

    protected EditText getPipelineLengthEditText() {
        return (EditText) findViewById(R.id.pipelineLengthTxt);
    }

    protected EditText getObservationsEditText() {
        return (EditText) findViewById(R.id.observations);
    }

    protected Spinner getTypeSpinner() {
        return (Spinner) findViewById(R.id.typeSpinner);
    }

    protected Spinner getStatusSpinner() {
        return (Spinner) findViewById(R.id.stateSpinner);
    }

    protected Spinner getPlumbSpinner() {
        return (Spinner) findViewById(R.id.plumbSpinner);
    }

    protected Spinner getDiameterSpinner() {
        return (Spinner) findViewById(R.id.diameterSpinner);
    }

    protected void getAddressFromLocation() {
        addressBean = AddressUtils.initAddressFromLocation((EditText) findViewById(R.id.addressTxt), (EditText) findViewById(R.id.neighborhoodTxt), getBaseContext(), lastLocation);
    }

    private void addSendButtonListener() {
        Button sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSendListenerAction();
            }
        });
    }

    @NonNull
    private AddressBean getAddressBean() {
        if (addressBean == null) {
            addressBean = new AddressBean();
        }
        AddressUtils.validateAddressFields(getBaseContext(),
                (EditText) findViewById(R.id.addressTxt),
                (EditText) findViewById(R.id.neighborhoodTxt),
                (EditText) findViewById(R.id.addressTitle),
                (EditText) findViewById(R.id.neighborhoodTitle), addressBean);

        return addressBean;
    }

    private void initLocationRequest() {

        mLocationRequest = new LocationRequest();
        AddressUtils.initLocationRequest(mLocationRequest);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
