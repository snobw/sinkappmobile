package com.inopek.duvana.sink.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.EditText;

import com.google.android.gms.location.LocationRequest;
import com.inopek.duvana.sink.beans.AddressBean;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.inopek.duvana.sink.services.CustomServiceUtils.hasText;
import static com.inopek.duvana.sink.utils.PropertiesUtils.getProperty;

public class AddressUtils {

    public static AddressBean initAddressFromLocation(EditText addressText, EditText neighborhoodText, Context context, Location lastLocation) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        AddressBean addressBean = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                addressBean = new AddressBean();
                Address firstAddress = addresses.get(0);
                addressBean.setCity(firstAddress.getAddressLine(1));
                addressBean.setCountry(firstAddress.getAddressLine(2));
                if (StringUtils.hasText(firstAddress.getSubLocality())) {
                    addressBean.setNeighborhood(firstAddress.getSubLocality());
                    neighborhoodText.setText(addressBean.getNeighborhood());
                }
                addressBean.setStreet(firstAddress.getAddressLine(0));
                addressBean.setComplementStreet(firstAddress.getAdminArea());
                addressBean.setZipCode(firstAddress.getPostalCode());
                addressText.setText(addressBean.getStreet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressBean;
    }

    public static AddressBean initAddressBeanFromUI(Context context, EditText addressText, EditText neighborhoodText, String addressDefaultMessage, String neigborhoodDefaultMessage) {
        AddressBean addressBean = new AddressBean();
        boolean addressExists = hasText(addressText, addressDefaultMessage);
        boolean neighborhoodExists = hasText(neighborhoodText, neigborhoodDefaultMessage);

        if (addressExists && neighborhoodExists) {
            try {
                addressBean.setCity(getProperty("duvana.default.city", context));
                addressBean.setCountry(getProperty("duvana.default.country", context));
            } catch (IOException e) {
            }
            addressBean.setStreet(addressText.getText().toString());
            addressBean.setNeighborhood(neighborhoodText.getText().toString());
        }
        return addressBean;
    }

    public static void initLocationRequest(LocationRequest mLocationRequest) {
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(10 * 1000);
        mLocationRequest.setSmallestDisplacement(1);
    }
}
