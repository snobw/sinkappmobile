package com.inopek.duvana.sink.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.SinkEditionActivity;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.tasks.HttpRequestDeleteSinkTask;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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
        Intent intent = new Intent(activity, SinkEditionActivity.class);
        Gson gson = new GsonBuilder().create();
        String jsonObject = gson.toJson(sinkBean, SinkBean.class);
        intent.putExtra("sinkBean", jsonObject);
        activity.startActivity(intent);
    }

    private void createDeleteAlertDialog(final SinkBean sinkBean) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // delete task
                        runDeleteTask(sinkBean);
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

    private void runDeleteTask(final SinkBean sinkBean) {
        if(sinkBean == null) {
            showToastMessage(getContext().getString(R.string.date_start_error_message), activity);
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
