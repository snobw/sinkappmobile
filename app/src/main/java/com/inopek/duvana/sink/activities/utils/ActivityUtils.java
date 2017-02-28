package com.inopek.duvana.sink.activities.utils;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbEnum;
import com.inopek.duvana.sink.enums.SinkStatutEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;

import java.util.Arrays;
import java.util.List;

public final class ActivityUtils {

    private ActivityUtils() {
    }

    public static void showToastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                .show();
    }

    public static void initTypeSpinner(Spinner spinner, Context context, String defaultMessage) {
        List<String> types = Arrays.asList(defaultMessage, SinkTypeEnum.COVENTIONAL.getLabel(), SinkTypeEnum.LATERAL.getLabel(), SinkTypeEnum.TRANSVERSAL.getLabel());
        mapSpinner(spinner, types, context);
    }

    public static void initStateSpinner(Spinner spinner, Context context, String defaultMessage) {
        List<String> status = Arrays.asList(defaultMessage, SinkStatutEnum.BAD.getLabel(), SinkStatutEnum.GOOD.getLabel(), SinkStatutEnum.MODERATE.getLabel());
        mapSpinner(spinner, status, context);
    }

    public static void initDiameterSpinner(Spinner spinner, Context context, String defaultMessage) {
        List<String> diameters = Arrays.asList(defaultMessage, SinkDiameterEnum.EIGHT.getLabel(), SinkDiameterEnum.TEN.getLabel(), SinkDiameterEnum.TWELVE.getLabel());
        mapSpinner(spinner, diameters, context);
    }

    public static void initPlumbSpinner(Spinner spinner, Context context, String defaultMessage) {
        List<String> options = Arrays.asList(defaultMessage, SinkPlumbEnum.YES.getLabel(), SinkPlumbEnum.NO.getLabel());
        mapSpinner(spinner, options, context);
    }

    private static void mapSpinner(Spinner spinner, List<String> elements, Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, elements);
        spinner.setAdapter(adapter);
    }
}
