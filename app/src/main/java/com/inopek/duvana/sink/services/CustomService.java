package com.inopek.duvana.sink.services;

import android.content.Context;
import android.graphics.Bitmap;

import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.SinkBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
     * delete all files with fileNames'name where flag is true
     * @param fileNames
     */
    void deleteFiles(HashMap<String, Boolean> fileNames, Context context);

    /**
     *
     * @param context
     * @param client
     * @param profile
     * @return
     */
    ArrayList<SinkBean> getAllSinksToSend(Context context, ClientBean client, String profile);

    /**
     * @return
     */
    ArrayList<SinkBean> getAllSinksSavedByDate(Context context, ClientBean client, String profile, Date startDate, Date endDate);

    /**
     *
     */
    void deleteTempImageFiles(Context context);
}
