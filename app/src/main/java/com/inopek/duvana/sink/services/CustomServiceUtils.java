package com.inopek.duvana.sink.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class CustomServiceUtils {

    private static final String REQUIRED_FIELD = "Campo obligatorio";
    private static final String REQUIRED_PHOTO = "Foto obligatoria";

    private CustomServiceUtils() {
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public static boolean isNumeric(EditText editText, String text) {
        editText.setError(null);

        if(!StringUtils.isNumeric(editText.getText().toString().trim())) {
            return setError(editText, text);
        }
        return true;
    }

    public static boolean hasText(EditText editText, String text) {

        editText.setError(null);

        // text required and editText is blank, so return false
        if (isEmpty(editText.getText().toString().trim())) {
            return setError(editText, text);
        }

        return true;
    }

    public static boolean isValidSpinner(Spinner spinner, TextView textView) {
        textView.setError(null);
        if(spinner.getSelectedItem().toString().equals(StringUtils.EMPTY)){
            //SpinnerAdapter adapter = spinner.getAdapter();
            textView.setVisibility(View.VISIBLE);
            textView.setError(REQUIRED_FIELD);
            return false;
        }
        return true;
    }

    private static boolean setError(EditText editText, String text) {
        editText.setText(text);
        editText.setError(REQUIRED_FIELD);
        return false;
    }

//    public static boolean isPhotoTaken(SinkBean sinkBean, TextView imageBeforeTextView, TextView imageAfterTextView) {
//        imageBeforeTextView.setError(null);
//        imageAfterTextView.setError(null);
//        boolean imageBeforeExists = isNotEmpty(sinkBean.getImageBefore());
//        boolean imageAfterExists = isNotEmpty(sinkBean.getImageAfter());
//        if(!imageBeforeExists) {
//            setErrorForImage(imageBeforeTextView);
//        }
//        if(!imageAfterExists) {
//            setErrorForImage(imageAfterTextView);
//        }
//        return imageAfterExists && imageBeforeExists;
//    }

    public static boolean photoExists(String encodedImage, TextView textView) {
        textView.setError(null);
        boolean imageExists = isNotEmpty(encodedImage);
        if(!imageExists) {
            setErrorForImage(textView);
        }
        return imageExists;
    }

    private static void setErrorForImage(TextView textView) {
        textView.setVisibility(View.VISIBLE);
        textView.setError(REQUIRED_PHOTO);
    }
}
