package com.inopek.duvana.sink.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.injectors.Injector;
import com.inopek.duvana.sink.services.CustomService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_DD_MM_YYYY;

public class SinkSendActivity extends AppCompatActivity {

    private List<CheckBox> checkBoxes = new ArrayList<>();

    @Inject
    CustomService customService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_send);
        // inject dependecies
        Injector.getInstance().getAppComponent().inject(this);
        createTable();
        addCheckBoxListener();
    }

    private void addCheckBoxListener() {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxAll);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox cb : checkBoxes) {
                    cb.setChecked(checkBox.isChecked());
                }
            }
        });
    }

    private void createTable() {

        TableLayout tableReport = (TableLayout) findViewById(R.id.tableReport);

        List<SinkBean> allSinksToSend = customService.getAllSinksToSend(getBaseContext());

        for (SinkBean sinkBean : allSinksToSend) {

            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundResource(R.drawable.table_gray);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            CheckBox checkBox = new CheckBox(this);
            checkBox.setTextColor(Color.CYAN);
            checkBox.setPadding(15, 5, 5, 5);
            checkBoxes.add(checkBox);
            tableRow.addView(checkBox);

            createView(tableRow, sinkBean.getReference());

            //createView(tableRow, sinkBean.getClient() != null ? sinkBean.getClient().getName() : StringUtils.EMPTY);

            createView(tableRow, sinkBean.getSinkCreationDate() != null ? DateFormatUtils.format(sinkBean.getSinkCreationDate(), DATE_FORMAT_DD_MM_YYYY) : StringUtils.EMPTY);

            tableReport.addView(tableRow);
        }
    }

    private void createView(TableRow tableRow, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(15, 5, 5, 5);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        tableRow.addView(textView);
    }
}
