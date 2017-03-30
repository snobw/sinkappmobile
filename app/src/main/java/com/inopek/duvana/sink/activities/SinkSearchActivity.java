package com.inopek.duvana.sink.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.adapters.SinkBeanEditionAdapter;
import com.inopek.duvana.sink.beans.ClientBean;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.enums.SearchTypeEnum;
import com.inopek.duvana.sink.fragment.DatePickerFragment;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.tasks.HttpRequestSearchSinkTask;
import com.inopek.duvana.sink.utils.DateUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
        initTypeSearch();
        addSearchListener();
        addExpandListener();
        addCollapseListener();
        // Clean temp file images
        cleanImagesFiles();
    }

    private void addCollapseListener() {
        getCollapseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getExpandButton().setVisibility(View.VISIBLE);
                getCollapseButton().setVisibility(View.GONE);
                getSearchArea().setVisibility(View.GONE);
            }
        });
    }

    private void addExpandListener() {
        getExpandButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getExpandButton().setVisibility(View.GONE);
                getCollapseButton().setVisibility(View.VISIBLE);
                getSearchArea().setVisibility(View.VISIBLE);
            }
        });
    }

    private void initTypeSearch() {
        final CharSequence[] types = {SearchTypeEnum.LOCAL.getLabel(), SearchTypeEnum.DISTANT.getLabel()};
        final EditText searchType = getTypeSearchEditText();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        searchType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setSingleChoiceItems(types, 0, clickListener())
                        .setTitle(getString(R.string.type_search_data_message))
                        .setPositiveButton(R.string.ok, okClickListener)
                        .show();

            }

            @NonNull
            private DialogInterface.OnClickListener clickListener() {
                return new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        searchType.setText(types[arg1]);
                        ListView lv = ((AlertDialog) dialog).getListView();
                        lv.setTag(new Integer(arg1));
                    }
                };
            }

            DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int choice) {
                    switch (choice) {
                        case DialogInterface.BUTTON_POSITIVE:
                            ListView lv = ((AlertDialog) dialog).getListView();
                            Integer selected = (Integer) lv.getTag();
                            if (selected == null) {
                                searchType.setText(types[0]);
                            }
                            break;
                    }
                }
            };
        });

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
        // check search type
        EditText startDateEditText = (EditText) findViewById(R.id.dateStartButton);
        EditText reference = (EditText) findViewById(R.id.referenceSearch);
        String startDateStr = startDateEditText.getText().toString();
        String typeSearch = getTypeSearchEditText().getText().toString();

        if (StringUtils.isEmpty(typeSearch)) {
            showToastMessage(getString(R.string.type_search_error_message), getBaseContext());
        } else  if(startDateStr == null) {
            showToastMessage(getString(R.string.date_start_error_message), getBaseContext());
        } else {
            EditText endDateEditText = (EditText) findViewById(R.id.dateEndButton);
            String endDateStr = endDateEditText.getText().toString();

            if(SearchTypeEnum.LOCAL.getLabel().equals(typeSearch)) {
                List<SinkBean> sinksFound = findLocalSinks(startDateStr, endDateStr, reference.getText().toString());
                populate(sinksFound);
                if(CollectionUtils.isEmpty(sinksFound)) {
                    showToastMessage(getString(R.string.search_try_later_message), getBaseContext());
                }
            } else if(ActivityUtils.isNetworkAvailable(getBaseContext())){
                // then lauch search distant
                runSearchTask(startDateStr, endDateStr, reference.getText().toString());
            } else {
                showToastMessage(getString(R.string.no_network_available_message), getBaseContext());
            }
        }
    }

    private void runSearchTask(final String startDate, final String endDate, final String reference) {

        new HttpRequestSearchSinkTask(startDate, endDate, reference, getBaseContext()) {

            ProgressDialog dialog;

            @Override
            protected void onPostExecute(SinkBean[] sinkBean) {
                dialog.dismiss();
                List<SinkBean> sinksFromBase = new ArrayList<>();
                if (sinkBean != null && sinkBean.length > 0) {
                    sinksFromBase = Arrays.asList(sinkBean);
                } else {
                    showToastMessage(getString(R.string.search_try_later_message), getBaseContext());
                }
                populate(sinksFromBase);
            }

            @Override
            protected void onPreExecute() {
                dialog = createDialog();
            }
        }.execute();

    }

    private ArrayList<SinkBean> findLocalSinks(String startDateStr, String endDateStr, String reference) {

        Date startDate = DateUtils.parseDateFromString(startDateStr, SinkConstants.DATE_FORMAT_DD_MM_YYYY);
        Date endDate = new DateTime().withTimeAtStartOfDay().toDate();
        if(endDateStr == null) {
            endDate = DateUtils.parseDateFromString(endDateStr, SinkConstants.DATE_FORMAT_DD_MM_YYYY);
        }
        String profile = ActivityUtils.getCurrentUserProfile(this);
        ClientBean client = ActivityUtils.getCurrentClient(this);
        return customService.getAllSinksSavedByDateAndReference(getBaseContext(), client, profile, startDate, endDate, reference);
    }

    private void populate(List<SinkBean> sinks) {
        ArrayList<SinkBean> results = new ArrayList<>();
        results.addAll(sinks);
        adapter = new SinkBeanEditionAdapter(getBaseContext(), results, R.layout.item_edit_sink, this);
        ListView listView = (ListView) findViewById(R.id.sinksListView);
        listView.setAdapter(adapter);
        if(CollectionUtils.isNotEmpty(results)) {
            getExpandButton().setVisibility(View.VISIBLE);
            getCollapseButton().setVisibility(View.GONE);
            getSearchArea().setVisibility(View.GONE);
        }
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

    private EditText getTypeSearchEditText() {
        return  (EditText) findViewById(R.id.searchType);
    }

    private Button getExpandButton() {
        return (Button) findViewById(R.id.expandButton);
    }

    private Button getCollapseButton() {
        return (Button) findViewById(R.id.collapseButton);
    }

    private LinearLayout getSearchArea() {
        return (LinearLayout) findViewById(R.id.searchArea);
    }
}
