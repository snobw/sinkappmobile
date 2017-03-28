package com.inopek.duvana.sink.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.SinkBasicEditionActivity;
import com.inopek.duvana.sink.activities.SinkEditionActivity;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.ClientReferenceBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.dao.ReferenceClientDao;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.tasks.HttpRequestDeleteSinkTask;
import com.inopek.duvana.sink.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.util.Base64.DEFAULT;
import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;

public class SinkBeanEditionAdapter extends AbstractSinkBeanAdapter {

    int resource;
    private Activity activity;
    private ArrayList<SinkBean> sinks;

    public SinkBeanEditionAdapter(Context context, ArrayList<SinkBean> sinks, int resource, Activity activity) {
        super(context, sinks);
        this.resource = resource;
        this.activity = activity;
        this.sinks = sinks;
    }

    @Override
    protected int getResource() {
        return resource;
    }

    @Override
    protected void populateCustomView(View convertView, final SinkBean sinkBean) {
        Button editButton = (Button) convertView.findViewById(R.id.editButton);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open edition
                edition(sinkBean);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDeleteAlertDialog(sinkBean);
            }
        });
    }

    private void edition(SinkBean sinkBean) {
        Gson gson = new GsonBuilder().create();
        String profilePreference = ActivityUtils.getStringPreference(activity, R.string.profile_name_preference, getContext().getString(R.string.profile_name_preference));
        String imageTmpPath;
        String reference = sinkBean.getReference();
        Intent intent = null;
        if(reference != null) {
            if (StringUtils.isNoneEmpty(sinkBean.getImageBefore())/*ProfileEnum.BEGIN.getLabel().equals(profilePreference) && sinkBean.getImageBefore() != null*/) {
                imageTmpPath = saveImage(sinkBean.getImageBefore(), reference);
                sinkBean.setImageBefore(imageTmpPath);
            }
            if (StringUtils.isNoneEmpty(sinkBean.getImageAfter()) /*ProfileEnum.END.getLabel().equals(profilePreference) && sinkBean.getImageAfter() != null*/) {
                imageTmpPath = saveImage(sinkBean.getImageAfter(), reference);
                sinkBean.setImageAfter(imageTmpPath);
            }
        }
        if (ProfileEnum.BEGIN.getLabel().equals(profilePreference)) {
            intent = new Intent(activity, SinkBasicEditionActivity.class);
        } else if (ProfileEnum.END.getLabel().equals(profilePreference)) {
            intent = new Intent(activity, SinkEditionActivity.class);
        }
        String jsonObject = gson.toJson(sinkBean, SinkBean.class);
        intent.putExtra("sinkBean", jsonObject);
        activity.startActivityForResult(intent, SinkConstants.EDITION_ACTIVITY_REQUEST_CODE);

    }

    private String saveImage(String base64, String reference) {
        try {
            DateTime dateTime = new DateTime();
            String fileName = reference + DateUtils.dateToString(dateTime, SinkConstants.DATE_FORMAT_YYYT_MM_DD_HH_mm_ss_SSS);
            String path = getContext().getCacheDir().getAbsolutePath()+ File.separator + "images" + File.separator; //Environment.getExternalStorageDirectory() + PropertiesUtils.getProperty("duvana.app.cache.path.images", getContext()) + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += fileName + ".png";
            if(base64.contains(path)) {
                // the path is already set, check file exists
                return  getFilePath(base64);
            }
            // decode and create file
            byte[] bytes = Base64.decode(base64, DEFAULT);
            File imageFile = new File(path);
            //File imageFile = File.createTempFile(fileName, null, getContext().getCacheDir());
            try (OutputStream stream = new FileOutputStream(imageFile)) {
                stream.write(bytes);
                stream.close();
            }
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("SinkBeanEditionAdapter", e.getMessage());
        }

        return null;
    }

    @Nullable
    private String getFilePath(String path) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    private void createDeleteAlertDialog(final SinkBean sinkBean) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // delete task
                        File file = sinkBean.getFileName() != null ? new File(sinkBean.getFileName()) : null;
                        if (file!= null && file.exists()) {
                            file.delete();
                            removeSink(sinkBean);
                            deleteFromDataBase(sinkBean);
                            notifyDataSetChanged();
                        } else if(sinkBean.getId() != null) {
                            runDeleteTask(sinkBean);
                            notifyDataSetChanged();
                        } else {
                            showToastMessage(getContext().getString(R.string.try_later_message), getContext());
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getContext().getString(R.string.delete_ask_message) + StringUtils.SPACE + sinkBean.getReference())
                .setPositiveButton(getContext().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getContext().getString(R.string.no), dialogClickListener).show();
    }

    private void deleteFromDataBase(SinkBean sinkBean) {
        String profilePreference = ActivityUtils.getStringPreference(activity, R.string.profile_name_preference, getContext().getString(R.string.profile_name_preference));
        ReferenceClientDao clientDao = new ReferenceClientDao(getContext());
        clientDao.open();
        ClientReferenceBean clientReferenceBean = new ClientReferenceBean(sinkBean.getReference(), sinkBean.getClient().getName(), sinkBean.getFileName(), profilePreference);
        clientDao.delete(clientReferenceBean);
        clientDao.close();
    }
    private void runDeleteTask(final SinkBean sinkBean) {
        if (sinkBean == null) {
            showToastMessage(getContext().getString(R.string.delete_try_later_message), activity);
        } else {
            new HttpRequestDeleteSinkTask(sinkBean, getContext()) {

                ProgressDialog dialog;

                @Override
                protected void onPostExecute(Boolean result) {
                    dialog.dismiss();
                    if (result != null && result) {
                        // delete from list
                        removeSink(sinkBean);
                        notifyDataSetChanged();
                    } else {
                        showToastMessage(getContext().getString(R.string.search_try_later_message), activity);
                    }
                }

                @Override
                protected void onPreExecute() {
                    dialog = createDialog();
                }
            }.execute();
        }
    }

    private ProgressDialog createDialog() {
        return ActivityUtils.createProgressDialog(getContext().getString(R.string.deleting_default_message), activity);
    }

    public void removeSink(SinkBean sinkBean) {
        sinks.remove(sinkBean);
    }
}
