package com.inopek.duvana.sink.activities.utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.adapters.SpinnerArrayAdapter;
import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.enums.SinkDiameterEnum;
import com.inopek.duvana.sink.enums.SinkPlumbOptionEnum;
import com.inopek.duvana.sink.enums.SinkStatusEnum;
import com.inopek.duvana.sink.enums.SinkTypeEnum;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class ActivityUtils {

    private static final String REQUIRED_FIELD = "Campo obligatorio";
    private static final String REQUIRED_PHOTO = "Foto obligatoria";

    private ActivityUtils() {
    }

    public static void showToastMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                .show();
    }

    public static void initTypeSpinner(Spinner spinner, Context context) {
        List<String> types = Arrays.asList(StringUtils.EMPTY, SinkTypeEnum.COVENTIONAL.getLabel(), SinkTypeEnum.LATERAL.getLabel(), SinkTypeEnum.TRANSVERSAL.getLabel());
        spinner.setAdapter(new SpinnerArrayAdapter(context, types));
    }

    public static void initStateSpinner(Spinner spinner, Context context) {
        List<String> status = Arrays.asList(StringUtils.EMPTY, SinkStatusEnum.BAD.getLabel(), SinkStatusEnum.GOOD.getLabel(), SinkStatusEnum.MODERATE.getLabel());
        spinner.setAdapter(new SpinnerArrayAdapter(context, status));
    }

    public static void initDiameterSpinner(Spinner spinner, Context context) {
        List<String> diameters = Arrays.asList(StringUtils.EMPTY, SinkDiameterEnum.EIGHT.getLabel(), SinkDiameterEnum.TEN.getLabel(), SinkDiameterEnum.TWELVE.getLabel());
        spinner.setAdapter(new SpinnerArrayAdapter(context, diameters));
    }

    public static void initPlumbSpinner(Spinner spinner, Context context) {
        List<String> options = Arrays.asList(StringUtils.EMPTY, SinkPlumbOptionEnum.YES.getLabel(), SinkPlumbOptionEnum.NO.getLabel());
        spinner.setAdapter(new SpinnerArrayAdapter(context, options));
    }

    public static ProgressDialog createProgressDialog(String message, final Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }

    public static void mapSpinner(Spinner spinner, List<String> elements, Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, elements);
        spinner.setAdapter(adapter);
    }

    public static String getStringPreference(final Activity activity, int id, String name) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(activity.getApplicationContext());
        String defaultValue = activity.getResources().getString(id);
        return sharedPref.getString(name, defaultValue);
    }

    public static void setDefaultClient(SinkBean sinkBean, final Activity activity, String name) {
        String clientName = getStringPreference(activity, R.string.client_name_preference, name);
        ClientBean clientBean = new ClientBean(clientName);
        sinkBean.setClient(clientBean);
    }

    public static UserBean getCurrentUser(final Activity activity) {
        String imeNumber = ActivityUtils.getStringPreference(activity, R.string.imei_name_preference, activity.getString(R.string.imei_name_preference));
        return new UserBean(imeNumber);
    }

    public static boolean isNumeric(EditText editText, EditText errorTxt) {
        errorTxt.setError(null);

        if (!StringUtils.isNumeric(editText.getText().toString().trim())) {
            return setError(errorTxt);
        }
        return true;
    }

    public static boolean hasText(EditText editText, EditText errorTxt) {

        errorTxt.setError(null);

        // text required and editText is blank, so return false
        if (isEmpty(editText.getText().toString().trim())) {
            return setError(errorTxt);
        }

        return true;
    }

    public static boolean isValidSpinner(Spinner spinner, TextView textView) {
        textView.setError(null);
        if (spinner.getSelectedItem().toString().equals(StringUtils.EMPTY)) {
            textView.setError(REQUIRED_FIELD);
            return false;
        }
        return true;
    }

    private static boolean setError(EditText editText) {
        editText.setError(REQUIRED_FIELD);
        return false;
    }

    public static boolean photoExists(String encodedImage, TextView textView) {
        textView.setError(null);
        boolean imageExists = isNotEmpty(encodedImage);
        if (!imageExists) {
            setErrorForImage(textView);
        }
        return imageExists;
    }

    private static void setErrorForImage(TextView textView) {
        textView.setVisibility(View.VISIBLE);
        textView.setError(REQUIRED_PHOTO);
    }

}
