package com.inopek.duvana.sink.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.adapters.SinkBeanAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.fragment.DatePickerFragment;
import com.inopek.duvana.sink.services.CustomService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import javax.inject.Inject;

public class SinkEditActivity extends AppCompatActivity {

    private static final String DATEPICKER_TAG = "datepicker";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(SinkConstants.DATE_FORMAT_DD_MM_YYYY);

    private SinkBeanAdapter adapter;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_edit);
        // inject dependecies
        initDatesAndDatesListeners();
        addSearchListener();
        //populate();
    }

    private void addSearchListener() {
        Button searchButoon = (Button) findViewById(R.id.searchButton);
        searchButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // search items and populate
                ArrayList<SinkBean> sinks = new ArrayList<>();
                SinkBean sinkBean = new SinkBean();
                sinkBean.setReference("789654");
                sinks.add(sinkBean);
                SinkBean sinkBean1= new SinkBean();
                sinkBean1.setReference("789654");
                sinks.add(sinkBean1);
                adapter = new SinkBeanAdapter(getBaseContext(), sinks, true, R.layout.item_edit_sink);
                ListView listView = (ListView) findViewById(R.id.sinksListView);
                listView.setAdapter(adapter);
            }
        });
    }

    private void initDatesAndDatesListeners() {
        DateTime todayDate = new DateTime();

        final EditText startDate = (EditText) findViewById(R.id.dateStartButton);
        final EditText endDate = (EditText) findViewById(R.id.dateEndButton);

        startDate.setText(todayDate.toString(DATE_TIME_FORMATTER));
        endDate.setText(todayDate.toString(DATE_TIME_FORMATTER));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateText(startDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDateText(endDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }



}
