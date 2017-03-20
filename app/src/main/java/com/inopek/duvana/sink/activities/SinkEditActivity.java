package com.inopek.duvana.sink.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SinkBeanEditionAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.fragment.DatePickerFragment;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSearchSinkTask;
import com.inopek.duvana.sink.utils.DateUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;
import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_DD_MM_YYYY;

public class SinkEditActivity extends AppCompatActivity {

    private SinkBeanEditionAdapter adapter;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_edit);
        // inject dependecies
        initDatesAndDatesListeners();
        addSearchListener();
    }

    private void addSearchListener() {
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // search items and populate
                EditText startDateEditText = (EditText) findViewById(R.id.dateStartButton);
                EditText endDateEditText = (EditText) findViewById(R.id.dateEndButton);
                runSearchTask(startDateEditText.getText().toString(), endDateEditText.getText().toString());
            }
        });
    }

    private void runSearchTask(String startDate, String endDate) {
        if(startDate == null) {
            showToastMessage(getString(R.string.date_start_error_message), getBaseContext());
        } else {
            new HttpRequestSearchSinkTask(startDate, endDate, getBaseContext()) {

                ProgressDialog dialog;

                @Override
                protected void onPostExecute(SinkBean[] sinkBean) {
                    dialog.dismiss();
                    if (sinkBean != null && sinkBean.length > 0) {
                        populate(Arrays.asList(sinkBean));
                    } else {
                        showToastMessage(getString(R.string.search_try_later_message), getBaseContext());
                    }
                }

                @Override
                protected void onPreExecute() {
                    dialog = createDialog();
                }
            }.execute();
        }
    }

    private void populate(List<SinkBean> sinks) {
        ArrayList<SinkBean> results = new ArrayList<>();
        results.addAll(sinks);
        adapter = new SinkBeanEditionAdapter(getBaseContext(), results, R.layout.item_edit_sink, this);
        ListView listView = (ListView) findViewById(R.id.sinksListView);
        listView.setAdapter(adapter);
    }

    private ProgressDialog createDialog() {
        return ActivityUtils.createProgressDialog(getString(R.string.searching_default_message), this);
    }

    private void initDatesAndDatesListeners() {
        DateTime todayDate = new DateTime();

        final EditText startDate = (EditText) findViewById(R.id.dateStartButton);
        final EditText endDate = (EditText) findViewById(R.id.dateEndButton);

        String todayDateStr = DateUtils.dateToString(todayDate, DATE_FORMAT_DD_MM_YYYY);
        startDate.setText(todayDateStr);
        endDate.setText(todayDateStr);

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
