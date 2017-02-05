package com.inopek.duvana.sink.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.AddressBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.enums.SinkStatutEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSaveTask;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class SinkCreationActivity extends AppCompatActivity {

    @Inject
    CustomService customService;

    private static final int BEFORE_REQUEST_CODE = 0;
    private static final int AFTER_REQUEST_CODE = 1;

    private ImageView imageViewBefore;
    private ImageView imageViewAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_creation);
        addCameraButtonListener();
        addSendButtonListener();
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        mapTypeSpinner();
        mapStateSpinner();
    }

    private void mapTypeSpinner() {
        List<String> types = Arrays.asList(SinkTypeEnum.COVENTIONAL.getLabel(), SinkTypeEnum.LATERAL.getLabel(), SinkTypeEnum.TRANSVERSAL.getLabel());
        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SinkCreationActivity.this,
                android.R.layout.simple_spinner_dropdown_item, types);
        spinner.setAdapter(adapter);
    }

    private void mapStateSpinner() {
        List<String> status = Arrays.asList(SinkStatutEnum.BAD.getLabel(), SinkStatutEnum.GOOD.getLabel(), SinkStatutEnum.MODERATE.getLabel());
        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SinkCreationActivity.this,
                android.R.layout.simple_spinner_dropdown_item, status);
        spinner.setAdapter(adapter);
    }

    private void addCameraButtonListener() {
        Button cameraButtonBefore = (Button) findViewById(R.id.cameraPreviousButton);
        Button cameraButtonAfter = (Button) findViewById(R.id.cameraFinalButton);
        imageViewBefore = (ImageView) findViewById(R.id.imageViewBefore);
        imageViewAfter = (ImageView) findViewById(R.id.imageViewAfter);

        cameraButtonBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, BEFORE_REQUEST_CODE);
            }
        });

        cameraButtonAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, AFTER_REQUEST_CODE);
            }
        });
    }

    private void addSendButtonListener() {
        Button sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestSaveTask(createSinkBean(), getBaseContext()).execute();
            }
        });
    }

    private SinkBean createSinkBean() {
        SinkBean sinkBean = new SinkBean();
        sinkBean.setAdresse(getAddressBean());
        SinkStatutEnum sinkStatutEnum = getSinkStatutEnum();
        SinkTypeEnum sinkTypeEnum = getSinkTypeEnum();
        EditText referenceText = (EditText) findViewById(R.id.referenceTxt);
        EditText lenghtText = (EditText) findViewById(R.id.lengthTxt);
        EditText pipeLineLenghtText = (EditText) findViewById(R.id.pipelineLengthTxt);
        EditText pipeLineDiametertText = (EditText) findViewById(R.id.pipelineLengthTxt);
        EditText observationsText = (EditText) findViewById(R.id.observations);
        if (sinkStatutEnum != null) {
            sinkBean.setSinkStatutId(sinkStatutEnum.getId());
        }
        if (sinkTypeEnum != null) {
            sinkBean.setSinkTypeId(sinkTypeEnum.getId());
        }
        sinkBean.setReference(referenceText.getText().toString());
        sinkBean.setObservations(observationsText.getText().toString());
        sinkBean.setLenght(Long.valueOf(lenghtText.getText().toString().trim()));
        sinkBean.setPipeLineLenght(Long.valueOf(pipeLineLenghtText.getText().toString().trim()));
        sinkBean.setPipeLineDiameter(Long.valueOf(pipeLineDiametertText.getText().toString().trim()));
        Bitmap imageViewBeforeDrawingCache = imageViewBefore.getDrawingCache();
        Bitmap imageViewAfterDrawingCache = imageViewAfter.getDrawingCache();
        if (imageViewBeforeDrawingCache != null) {
            sinkBean.setImageBefore(customService.encodeBase64(imageViewBeforeDrawingCache));
        }
        if (imageViewAfterDrawingCache != null) {
            sinkBean.setImageAfter(customService.encodeBase64(imageViewAfterDrawingCache));
        }
        imageViewBefore.setDrawingCacheEnabled(false);
        imageViewAfter.setDrawingCacheEnabled(false);
        return sinkBean;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        if (BEFORE_REQUEST_CODE == requestCode) {
            imageViewBefore.setImageBitmap(bp);
            imageViewBefore.setDrawingCacheEnabled(true);
        }
        if (AFTER_REQUEST_CODE == requestCode) {
            imageViewAfter.setImageBitmap(bp);
            imageViewAfter.setDrawingCacheEnabled(true);
        }
    }

    @NonNull
    private AddressBean getAddressBean() {
        EditText addressText = (EditText) findViewById(R.id.addressTxt);
        AddressBean address = new AddressBean();
        address.setCity("Bogota");
        address.setCountry("Colombia");
        address.setStreet(addressText.getText().toString());
        return address;
    }

    private SinkStatutEnum getSinkStatutEnum() {
        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        String selectedItem = (String) spinner.getSelectedItem();
        return SinkStatutEnum.getSinkStatutEnumByName(selectedItem);
    }

    private SinkTypeEnum getSinkTypeEnum() {
        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        String selectedItem = (String) spinner.getSelectedItem();
        return SinkTypeEnum.getSinkStatutEnumByName(selectedItem);
    }
}
