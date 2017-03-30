package com.inopek.duvana.sink.services.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.ClientReferenceBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.dao.ReferenceClientDao;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.services.CustomServiceUtils;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_YYYT_MM_DD;
import static com.inopek.duvana.sink.constants.SinkConstants.FILE_NAME_SEPARATOR;
import static com.inopek.duvana.sink.constants.SinkConstants.JSON_EXTENSION;

public class CustomServiceImpl implements CustomService {

    @Override
    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public String encodeBase64(Bitmap bitmap) {
        return CustomServiceUtils.encodeTobase64(bitmap);
    }

    @Override
    public boolean createAndSaveFile(SinkBean sinkBean, Context context) {
        try {
            File data;
            if(!fileExists(sinkBean)) {
                String path = Environment.getExternalStorageDirectory() + PropertiesUtils.getProperty("duvana.app.cache.path", context) + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                path += UUID.randomUUID().toString() + FILE_NAME_SEPARATOR + DateFormatUtils.format(new Date(), DATE_FORMAT_YYYT_MM_DD) + JSON_EXTENSION;
                data = new File(path);
                if (!data.createNewFile()) {
                    data.delete();
                    data.createNewFile();
                }
                sinkBean.setFileName(path);
            } else {
                data = new File(sinkBean.getFileName());
            }
            Gson gson = new GsonBuilder().create();
            String toJson = gson.toJson(sinkBean, SinkBean.class);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(data), SinkConstants.ENCODING_DEFAULT_NAME));
            bw.write(toJson);
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void deleteFiles(HashMap<String, Boolean> fileNamesMap, Context context) {
        for (Map.Entry<String, Boolean> entry : fileNamesMap.entrySet()) {
            if(BooleanUtils.isTrue(entry.getValue())) {
                // delete file
                String fileName = entry.getKey();
                File file = new File(fileName);
                file.delete();
                // and delete entry from database

                ReferenceClientDao dao = new ReferenceClientDao(context);
                dao.open();
                dao.deleteByFileName(fileName);
                dao.close();
            }
        }
    }

    @Override
    public ArrayList<SinkBean> getAllSinksToSend(Context context, ClientBean client, String profile) {
        ArrayList<SinkBean> sinkBeans = new ArrayList<>();
        ReferenceClientDao dao = new ReferenceClientDao(context);
        dao.open();
        List<ClientReferenceBean> results = dao.getByClientNameAndProfile(client.getName(), profile);
        dao.close();
        try {
            createSinkBeansFromFile(sinkBeans, results);
        } catch (IOException ex) {
            Log.e("CustomServiceImpl", "Error while reading file " + ex.getMessage());
        }
        return sinkBeans;
    }

    @Override
    public ArrayList<SinkBean> getAllSinksSavedByDateAndReference(final Context context, ClientBean client, String profile, Date startDate, Date endDate, String reference) {
        ArrayList<SinkBean> sinkBeans = new ArrayList<>();
        ReferenceClientDao dao = new ReferenceClientDao(context);
        dao.open();
        List<ClientReferenceBean> results = dao.getByClientNameAndProfileByDateAndReferene(client.getName(), profile, startDate, endDate, reference);
        dao.close();
        try {
            createSinkBeansFromFile(sinkBeans, results);
        } catch (IOException ex) {
            Log.e("CustomServiceImpl", "Error while reading file " + ex.getMessage());
        }

        return sinkBeans;
    }

    @Override
    public void deleteTempImageFiles(Context context) {
        String path = context.getCacheDir().getAbsolutePath()+ File.separator + "images";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void createSinkBeansFromFile(ArrayList<SinkBean> sinkBeans, List<ClientReferenceBean> clientReferences) throws IOException {
        Reader reader;
        if (CollectionUtils.isNotEmpty(clientReferences)) {
            for (ClientReferenceBean clientReference : clientReferences) {
                File file = new File(clientReference.getFileName());
                if(file.isFile()) {
                    reader = new FileReader(file);
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    SinkBean sinkBean = gson.fromJson(reader, SinkBean.class);
                    if (sinkBean != null) {
                        sinkBean.setFileName(file.getAbsolutePath());
                        sinkBeans.add(sinkBean);
                    }
                    reader.close();
                }
            }
        }
    }

    private boolean fileExists(SinkBean sinkBean) {
        if(sinkBean.getFileName() != null) {
            File file = new File(sinkBean.getFileName());
            return file.isFile();
        }
        return false;
    }
}
