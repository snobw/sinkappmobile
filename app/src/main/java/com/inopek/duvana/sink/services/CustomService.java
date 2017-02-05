package com.inopek.duvana.sink.services;

import android.content.Context;
import android.graphics.Bitmap;

public interface CustomService {

    boolean checkCameraHardware(Context context);

    String encodeBase64(Bitmap bitmap);

}
