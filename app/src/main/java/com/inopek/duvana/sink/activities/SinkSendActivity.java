package com.inopek.duvana.sink.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SinkBeanSendAdapter;
import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSendFileTask;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;

public class SinkSendActivity extends AppCompatActivity {

    private SinkBeanSendAdapter adapter;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_send);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        populate(null);
        addCheckBoxListener();
        addSendListener();
    }

    private void addSendListener() {
        final Button sendButton = (Button) findViewById(R.id.sendSinkButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<SinkBean> sinks = adapter.getSinks();
                if(!ActivityUtils.isNetworkAvailable(getBaseContext())) {
                    showToastMessage(getString(R.string.no_network_available_message), getBaseContext());
                } else if (CollectionUtils.isEmpty(sinks)) {
                    showToastMessage(getString(R.string.empty_list_send_message), getBaseContext());
                } else {
                    //send report
                    runTask(sinks);
                }
            }
        });
    }

    private void runTask(Set<SinkBean> sinks) {
        String identifier = ActivityUtils.getStringPreference(this, R.string.imei_name_preference, getString(R.string.imei_name_preference));
        if(getString(R.string.imei_name_preference).equals(identifier)) {
            showToastMessage(getString(R.string.imei_error), getBaseContext());
        } else {
            UserBean userBean = new UserBean(identifier);
            new HttpRequestSendFileTask(sinks, getBaseContext(), userBean) {

                ProgressDialog dialog;

                @Override
                protected void onPostExecute(HashMap<String, Boolean> fileNamesMap) {
                    dialog.dismiss();
                    if (CollectionUtils.isEmpty(fileNamesMap)) {
                        showToastMessage(getString(R.string.try_later_message), getBaseContext());
                    } else {
                        customService.deleteFiles(fileNamesMap, getBaseContext());
                        populate(fileNamesMap);
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
        return ActivityUtils.createProgressDialog(getString(R.string.sending_default_message), this);
    }

    private void addCheckBoxListener() {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxAll);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null && !CollectionUtils.isEmpty(adapter.getCheckBoxes())) {
                    for (CheckBox cb : adapter.getCheckBoxes()) {
                        cb.setChecked(checkBox.isChecked());
                        if (cb.isChecked()) {
                            adapter.addSink((SinkBean) cb.getTag());
                        } else {
                            adapter.removeSink((SinkBean) cb.getTag());
                        }
                    }
                }
            }
        });
    }

    private void populate(HashMap<String, Boolean> fileNamesMap) {
        // Construct the data source
        String profile = ActivityUtils.getCurrentUserProfile(this);
        ClientBean client = ActivityUtils.getCurrentClient(this);
        ArrayList<SinkBean> sinks = customService.getAllSinksToSend(getBaseContext(), client, profile);
        // Create the adapter to convert the array to views
        adapter = new SinkBeanSendAdapter(this, sinks, R.layout.item_sink, fileNamesMap);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.sinksListView);
        listView.setAdapter(adapter);
    }
}
