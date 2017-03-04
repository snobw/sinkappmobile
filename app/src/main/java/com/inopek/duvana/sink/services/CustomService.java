package com.inopek.duvana.sink.services;

import android.content.Context;
import android.graphics.Bitmap;

import com.inopek.duvana.sink.beans.SinkBean;

import java.util.ArrayList;
import java.util.List;

public interface CustomService {

    /**
     * @param context
     * @return
     */
    boolean checkCameraHardware(Context context);

    /**
     * @param bitmap
     * @return
     */
    String encodeBase64(Bitmap bitmap);

    /**
     * @param sinkBean
     */
    boolean createAndSaveFile(SinkBean sinkBean, Context context);

    /**
     * delete all files with fileNames'name
     * @param fileNames
     */
    void deleteFiles(List<String> fileNames);

    /**
     * @return
     */
    ArrayList<SinkBean> getAllSinksToSend(Context context);

}
