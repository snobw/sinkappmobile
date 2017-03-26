package com.inopek.duvana.sink.services.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.services.CustomServiceUtils;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            Gson gson = new GsonBuilder().create();
            String toJson = gson.toJson(sinkBean, SinkBean.class);
            File data;
            if(!fileExists(sinkBean)) {
                String path = Environment.getExternalStorageDirectory() + File.separator + PropertiesUtils.getProperty("duvana.app.cache.path", context) + File.separator;
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
            } else {
                data = new File(sinkBean.getFileName());
            }
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
    public void deleteFiles(List<String> fileNames) {
        for(String fileName : fileNames) {
            File file = new File(fileName);
            file.delete();
        }
    }

    @Override
    public ArrayList<SinkBean> getAllSinksToSend(Context context) {
        ArrayList<SinkBean> sinkBeans = new ArrayList<>();
        try {
            File del = new File(Environment.getExternalStorageDirectory() + PropertiesUtils.getProperty("duvana.app.cache.path.images", context));
            del.delete();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PropertiesUtils.getProperty("duvana.app.cache.path", context);
            File directory = new File(path);
            File[] files = directory.listFiles();
            createSinkBeansFromFile(sinkBeans, files);

        } catch (IOException ex) {
            Log.e("CustomServiceImpl", "Error while reading file " + ex.getMessage());
        }
        return sinkBeans;
    }

    @Override
    public ArrayList<SinkBean> getAllSinksSaved(final Context context, final Date startDate, final Date endDate) {
        ArrayList<SinkBean> sinkBeans = new ArrayList<>();
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PropertiesUtils.getProperty("duvana.app.cache.path", context);
            File directory = new File(path);
            FileFilter filter = new FileFilter() {
                public boolean accept(File file) {
                    if (!file.isFile()) {
                        return false;
                    }
                    DateTime fileDate = new DateTime(file.lastModified()).withTimeAtStartOfDay();
                    if ((fileDate.isAfter(startDate.getTime()) || fileDate.isEqual(startDate.getTime())) && (fileDate.isBefore(endDate.getTime()) || fileDate.isEqual(endDate.getTime()))) {
                        return true;
                    }
                    return false;
                }
            };
            File[] myFiles = directory.listFiles(filter);
            createSinkBeansFromFile(sinkBeans, myFiles);
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

    private void createSinkBeansFromFile(ArrayList<SinkBean> sinkBeans, File[] files) throws IOException {
        Reader reader;
        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
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

    private boolean fileExists(SinkBean sinkBean) {
        if(sinkBean.getFileName() != null) {
            File file = new File(sinkBean.getFileName());
            return file.isFile();
        }
        return false;
    }
}
