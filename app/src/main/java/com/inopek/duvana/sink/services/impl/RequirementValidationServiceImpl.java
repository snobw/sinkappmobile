package com.inopek.duvana.sink.services.impl;

import android.content.Context;
import android.content.pm.PackageManager;

import com.inopek.duvana.sink.services.RequirementValidationService;

public class RequirementValidationServiceImpl implements RequirementValidationService {

    @Override
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
