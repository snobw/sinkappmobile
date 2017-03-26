package com.inopek.duvana.sink.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SinkBeanEditionAdapter;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.fragment.DatePickerFragment;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSearchSinkTask;
import com.inopek.duvana.sink.utils.DateUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;
import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_DD_MM_YYYY;

public class SinkSearchActivity extends AppCompatActivity {

    private SinkBeanEditionAdapter adapter;

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_search);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        initDatesAndDatesListeners();
        addSearchListener();
        // Clean temp file images
        cleanImagesFiles();
    }

    private void cleanImagesFiles() {
       customService.deleteTempImageFiles(getBaseContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(SinkConstants.EDITION_ACTIVITY_REQUEST_CODE == requestCode && SinkConstants.EDITION_ACTIVITY_RESULT_CODE == resultCode ) {
            launchSearch();
        }
    }

    private void addSearchListener() {
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // search items and populate
                launchSearch();
            }
        });
    }

    private void launchSearch() {
        EditText startDateEditText = (EditText) findViewById(R.id.dateStartButton);
        EditText endDateEditText = (EditText) findViewById(R.id.dateEndButton);
        // search local files first
        List<SinkBean> sinksFound = new ArrayList<>();
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();
        findLocalSinks(startDateStr, endDateStr, sinksFound);
        // then lauch search distant
        runSearchTask(startDateStr, endDateStr, sinksFound);
    }

    private void runSearchTask(final String startDate, final String endDate, final List<SinkBean> sinksFound) {
        if(startDate == null) {
            showToastMessage(getString(R.string.date_start_error_message), getBaseContext());
        } else {
            new HttpRequestSearchSinkTask(startDate, endDate, getBaseContext()) {

                ProgressDialog dialog;

                @Override
                protected void onPostExecute(SinkBean[] sinkBean) {
                    dialog.dismiss();
                    if (sinkBean != null && sinkBean.length > 0) {
                        List<SinkBean> sinksFromBase = Arrays.asList(sinkBean);
                        if(CollectionUtils.isNotEmpty(sinksFromBase)) {
                            sinksFound.addAll(sinksFromBase);
                        }
                    }

                    if(CollectionUtils.isNotEmpty(sinksFound)) {
                        populate(sinksFound);
                    }
                    else {
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

    private void findLocalSinks(String startDateStr, String endDateStr, List<SinkBean> sinksFound) {
        final String profile = ActivityUtils.getStringPreference(this, R.string.profile_name_preference, getString(R.string.profile_name_preference));
        Date startDate = DateUtils.parseDateFromString(startDateStr, SinkConstants.DATE_FORMAT_DD_MM_YYYY);
        Date endDate = new DateTime().withTimeAtStartOfDay().toDate();
        if(endDateStr == null) {
            endDate = DateUtils.parseDateFromString(endDateStr, SinkConstants.DATE_FORMAT_DD_MM_YYYY);
        }
        ArrayList<SinkBean> allSinksSaved = customService.getAllSinksSaved(getBaseContext(), startDate, endDate);
        CollectionUtils.filter(allSinksSaved, new Predicate() {

            @Override
            public boolean evaluate(Object o) {
                SinkBean sinkBean = (SinkBean) o;
                return ProfileEnum.BEGIN.getLabel().equals(profile) ? sinkBean.getImageBefore() != null : ProfileEnum.END.getLabel().equals(profile) && sinkBean.getImageAfter() != null;
            }
        });
        sinksFound.addAll(allSinksSaved);
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
                newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
