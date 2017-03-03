package com.inopek.duvana.sink.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SinkBeanAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSaveTask;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

public class SinkSendActivity extends AppCompatActivity {

    private SinkBeanAdapter adapter;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_send);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        populate();
        addCheckBoxListener();
        addSendListener();
    }

    private void addSendListener() {
        final Button sendButton = (Button) findViewById(R.id.sendSinkButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<SinkBean> sinks = adapter.getSinks();
                if (CollectionUtils.isEmpty(sinks)) {
                    ActivityUtils.showToastMessage(getString(R.string.empty_list_send_message), getBaseContext());
                } else {
                    //send report
                    AsyncTask<Void, Void, SinkBean> execute = new HttpRequestSaveTask(sinks, getBaseContext()).execute();
                }
            }
        });
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

    private void populate() {
        // Construct the data source
        ArrayList<SinkBean> sinks = customService.getAllSinksToSend(getBaseContext());
        // Create the adapter to convert the array to views
        adapter = new SinkBeanAdapter(this, sinks);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.sinksListView);
        listView.setAdapter(adapter);

    }
}
