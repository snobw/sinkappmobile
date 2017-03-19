package com.inopek.duvana.sink.activities;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
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
import com.inopek.duvana.sink.tasks.HttpRequestSendBeanTask;
import com.inopek.duvana.sink.utils.AddressUtils;

import java.util.Arrays;
import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initDiameterSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initPlumbSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initStateSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.initTypeSpinner;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.setDefaultClient;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;
import static com.inopek.duvana.sink.services.CustomServiceUtils.hasText;
import static com.inopek.duvana.sink.services.CustomServiceUtils.isNumeric;
import static com.inopek.duvana.sink.services.CustomServiceUtils.isValidSpinner;
import static com.inopek.duvana.sink.services.CustomServiceUtils.photoExists;

public class SinkCreationActivity extends AbstractCreationActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private LocationRequest mLocationRequest;
    private AddressBean addressBean;


    @Override
    protected void customInitialize() {
        setContentView(R.layout.activity_sink_creation);
        addCameraButtonListener((Button) findViewById(R.id.cameraFinalButton));
        addSendButtonListener();
        addSaveSendLaterButtonListener((Button) findViewById(R.id.saveAndSendLaterButton));
        Context context = getBaseContext();
        initTypeSpinner((Spinner) findViewById(R.id.typeSpinner), context, getString(R.string.type_default_message));
        initStateSpinner((Spinner) findViewById(R.id.stateSpinner), context, getString(R.string.state_default_message));
        initDiameterSpinner((Spinner) findViewById(R.id.diameterSpinner), context, getString(R.string.diameter_default_message));
        initPlumbSpinner((Spinner) findViewById(R.id.plumbSpinner), context, getString(R.string.plumb_default_message));
        initLocationRequest();
    }

    private void initLocationRequest() {

        mLocationRequest = new LocationRequest();
        AddressUtils.initLocationRequest(mLocationRequest);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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

    private void addSendButtonListener() {
        Button sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinkBean sinkBean = new SinkBean();
                if (createSinkBean(sinkBean)) {
                    runTask(sinkBean, true);
                }
            }
        });
    }

    private void runTask(final SinkBean sink, final boolean checkReferenceExists) {

        new HttpRequestSendBeanTask(sink, getBaseContext(), ActivityUtils.getCurrentUser(this), checkReferenceExists) {

            ProgressDialog dialog;
            @Override
            protected void onPostExecute(Long id) {
                dialog.dismiss();

                if (id == null) {
                    showToastMessage(getString(R.string.try_later_message), getBaseContext());
                } else if(id == 0L) {
                    createAlertReferenceDialog(sink);
                } else {
                    showToastMessage(getString(R.string.success_save_message), getBaseContext());
                    finish();
                }
            }

            @Override
            protected void onPreExecute() {
                dialog = createDialog();
            }
        }.execute();

    }

    private ProgressDialog createDialog() {
        return ActivityUtils.createProgressDialog(getString(R.string.sending_default_message), this);
    }

    private void createAlertReferenceDialog(final SinkBean sinkBean) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // send again
                        runTask(sinkBean, false);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.reference_exists_save_message))
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected boolean createSinkBean(SinkBean sinkBean) {

        sinkBean.setSinkCreationDate(new Date());
        sinkBean.setAddress(getAddressBean());

        Bitmap imageViewAfterDrawingCache = getImageView().getDrawingCache();

        if (imageViewAfterDrawingCache != null) {
            sinkBean.setImageAfter(customService.encodeBase64(imageViewAfterDrawingCache));
        }
        getImageView().setDrawingCacheEnabled(true);
        setDefaultClient(sinkBean, this, getString(R.string.client_name_preference));

        boolean referenceExists = referenceExists(sinkBean);
        boolean validSinkStatusEnum = isValidSinkStatusEnum(sinkBean);
        boolean validSinkTypeEnum = isValidSinkTypeEnum(sinkBean);
        boolean validSinkDiameterEnum = isValidSinkDiameterEnum(sinkBean);
        boolean validSinkPlumbEnum = isValidSinkPlumbEnum(sinkBean);
        boolean photoExists = isPhotoTaken(sinkBean);

        return referenceExists && validSinkStatusEnum && validSinkTypeEnum && validSinkDiameterEnum && validSinkPlumbEnum && photoExists;
    }

    @Override
    protected ImageView getImageView() {
        return (ImageView) findViewById(R.id.imageViewAfter);
    }

    @Override
    protected TextView getTextViewImage() {
        return (TextView) findViewById(R.id.imageAfterTextView);
    }

    private boolean referenceExists(SinkBean sinkBean) {

        EditText referenceText = (EditText) findViewById(R.id.referenceTxt);
        EditText observationsText = (EditText) findViewById(R.id.observations);

        boolean referenceExist = hasText(referenceText, getString(R.string.reference_default_message));

        if (referenceExist) {
            sinkBean.setReference(referenceText.getText().toString());
            sinkBean.setObservations(observationsText.getText().toString());
        }
        return referenceExist;
    }

    @NonNull
    private AddressBean getAddressBean() {
        if (addressBean == null) {
            addressBean = AddressUtils.initAddressBeanFromUI(getBaseContext(),
                    (EditText) findViewById(R.id.addressTxt),
                    (EditText) findViewById(R.id.neighborhoodTxt),
                    getString(R.string.address_default_message),
                    getString(R.string.neighborhood_default_message));
        }
        return addressBean;
    }

    private boolean isValidSinkStatusEnum(SinkBean sinkBean) {
        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        TextView textView = (TextView) findViewById(R.id.stateErrorTextView);
        textView.setVisibility(View.INVISIBLE);
        if (isValidSpinner(spinner, getString(R.string.state_default_message), textView)) {
            String selectedItem = (String) spinner.getSelectedItem();
            sinkBean.setSinkStatusId(SinkStatusEnum.getSinkStatutEnumByName(selectedItem).getId());
            return true;
        }
        return false;
    }

    private boolean isValidSinkTypeEnum(SinkBean sinkBean) {

        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        TextView textView = (TextView) findViewById(R.id.typeErrorTextView);
        textView.setVisibility(View.INVISIBLE);
        if (isValidSpinner(spinner, getString(R.string.type_default_message), textView)) {
            String selectedItem = (String) spinner.getSelectedItem();
            SinkTypeEnum sinkTypeEnum = SinkTypeEnum.getSinkTypeEnum(selectedItem);
            if (Arrays.asList(SinkTypeEnum.LATERAL, SinkTypeEnum.TRANSVERSAL).contains(sinkTypeEnum)) {
                EditText lengthText = (EditText) findViewById(R.id.lengthTxt);
                boolean lengthExists = isNumeric(lengthText, getString(R.string.length_default_message));
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
        Spinner spinner = (Spinner) findViewById(R.id.diameterSpinner);
        TextView textView = (TextView) findViewById(R.id.diameterErrorTextView);
        textView.setVisibility(View.INVISIBLE);
        if (isValidSpinner(spinner, getString(R.string.diameter_default_message), textView)) {
            String selectedItem = (String) spinner.getSelectedItem();
            sinkBean.setPipeLineDiameterId(SinkDiameterEnum.getSinkDiameterEnum(selectedItem).getId());
            return true;
        }
        return false;
    }

    private boolean isValidSinkPlumbEnum(SinkBean sinkBean) {
        Spinner spinner = (Spinner) findViewById(R.id.plumbSpinner);
        TextView textView = (TextView) findViewById(R.id.plumbErrorTextView);
        textView.setVisibility(View.INVISIBLE);
        if (isValidSpinner(spinner, getString(R.string.plumb_default_message), textView)) {
            String selectedItem = (String) spinner.getSelectedItem();
            SinkPlumbOptionEnum sinkPlumbOptionEnum = SinkPlumbOptionEnum.getSinkPlumbEnum(selectedItem);
            if (SinkPlumbOptionEnum.YES.equals(sinkPlumbOptionEnum)) {
                EditText pipeLineLenghtText = (EditText) findViewById(R.id.pipelineLengthTxt);
                boolean lengthExists = isNumeric(pipeLineLenghtText, getString(R.string.length_default_message));
                if (lengthExists) {
                    sinkBean.setPipeLineLength(Long.valueOf(pipeLineLenghtText.getText().toString().trim()));
                } else {
                    return false;
                }
            }
            sinkBean.setPlumbOptionId(sinkPlumbOptionEnum.getId());
            return true;
        }
        return false;
    }

    private boolean isPhotoTaken(SinkBean sinkBean) {
        TextView textView = (TextView) findViewById(R.id.imageAfterTextView);
        textView.setVisibility(View.INVISIBLE);
        return photoExists(sinkBean.getImageAfter(), textView);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ActivityUtils.showToastMessage(getString(R.string.address_gps_error_message), getBaseContext());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation != null) {
            getAddressFromLocation();
        }

        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    private void getAddressFromLocation() {
        addressBean = AddressUtils.initAddressFromLocation((EditText) findViewById(R.id.addressTxt), (EditText) findViewById(R.id.neighborhoodTxt), getBaseContext(), lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        ActivityUtils.showToastMessage(getString(R.string.address_gps_error_message), getBaseContext());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        if (location != null) {
            lastLocation = location;
            addressBean = AddressUtils.initAddressFromLocation((EditText) findViewById(R.id.addressTxt), (EditText) findViewById(R.id.neighborhoodTxt), getBaseContext(), lastLocation);
        }
    }


}
