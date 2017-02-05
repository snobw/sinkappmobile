package com.inopek.duvana.sink.services.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.services.CustomServiceUtils;

public class CustomServiceImpl implements CustomService {

    @Override
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public String encodeBase64(Bitmap bitmap) {
        return CustomServiceUtils.encodeTobase64(bitmap);
    }
}
