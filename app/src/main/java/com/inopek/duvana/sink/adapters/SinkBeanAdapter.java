package com.inopek.duvana.sink.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_DD_MM_YYYY;

public class SinkBeanAdapter extends ArrayAdapter<SinkBean> {

    private Set<SinkBean> sinks = new HashSet<>();

    private List<CheckBox> checkBoxes = new ArrayList<>();

    private boolean edition;

    int resource;

    public SinkBeanAdapter(Context context, ArrayList<SinkBean> sinks, boolean edition, int resource) {
        super(context, 0, sinks);
        this.edition = edition;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SinkBean sinkBean = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        // Lookup view for data population
        TextView referenceTv = (TextView) convertView.findViewById(R.id.referenceTextView);
        if (!edition) {
            addViewsForNoEdition(convertView, sinkBean);
        } else {
            addViewsForEdition(convertView, sinkBean);
        }
        // Populate the data into the template view using the data object
        referenceTv.setText(sinkBean.getReference());

        // Return the completed view to render on screen
        return convertView;
    }

    private void addViewsForNoEdition(View convertView, SinkBean sinkBean) {
        TextView dateTv = (TextView) convertView.findViewById(R.id.dateTextView);
        CheckBox itemCheckBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
        itemCheckBox.setTag(sinkBean);
        addCheckListener(itemCheckBox);
        checkBoxes.add(itemCheckBox);
        dateTv.setText(sinkBean.getSinkCreationDate() != null ? DateFormatUtils.format(sinkBean.getSinkCreationDate(), DATE_FORMAT_DD_MM_YYYY) : StringUtils.EMPTY);
    }

    private void addViewsForEdition(View convertView, SinkBean sinkBean) {

        Button editButton = (Button) convertView.findViewById(R.id.editButton);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open edition
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ask user he's sure about it
            }
        });
    }

    private void addCheckListener(CheckBox checkBox) {

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SinkBean sinkBean = (SinkBean) view.getTag();
                if (((AppCompatCheckBox) view).isChecked()) {
                    sinks.add(sinkBean);
                } else {
                    sinks.remove(sinkBean);
                }
            }
        });
    }

    public Set<SinkBean> getSinks() {
        return sinks;
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void addSink(SinkBean sinkBean) {
        sinks.add(sinkBean);
    }

    public void removeSink(SinkBean sinkBean) {
        sinks.remove(sinkBean);
    }
}
