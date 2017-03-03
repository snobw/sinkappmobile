package com.inopek.duvana.sink.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public SinkBeanAdapter(Context context, ArrayList<SinkBean> sinks) {
        super(context, 0, sinks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SinkBean sinkBean = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sink, parent, false);
        }
        // Lookup view for data population
        TextView referenceTv = (TextView) convertView.findViewById(R.id.referenceTextView);
        TextView dateTv = (TextView) convertView.findViewById(R.id.dateTextView);
        CheckBox itemCheckBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
        itemCheckBox.setTag(sinkBean);
        addCheckListener(itemCheckBox);
        checkBoxes.add(itemCheckBox);

        // Populate the data into the template view using the data object
        referenceTv.setText(sinkBean.getReference());
        dateTv.setText(sinkBean.getSinkCreationDate() != null ? DateFormatUtils.format(sinkBean.getSinkCreationDate(), DATE_FORMAT_DD_MM_YYYY) : StringUtils.EMPTY);
        // Return the completed view to render on screen
        return convertView;
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
