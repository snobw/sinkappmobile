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

public class SinkBeanSendAdapter extends AbstractSinkBeanAdapter {

    private Set<SinkBean> sinks = new HashSet<>();

    private List<CheckBox> checkBoxes = new ArrayList<>();

    int resource;

    public SinkBeanSendAdapter(Context context, ArrayList<SinkBean> sinks, int resource) {
        super(context, sinks);
        this.resource = resource;
    }

    @Override
    protected int getResource() {
        return resource;
    }

    @Override
    protected void populateCustomView(View convertView, SinkBean sinkBean) {
        TextView dateTv = (TextView) convertView.findViewById(R.id.dateTextView);
        CheckBox itemCheckBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
        itemCheckBox.setTag(sinkBean);
        addCheckListener(itemCheckBox);
        checkBoxes.add(itemCheckBox);
        dateTv.setText(sinkBean.getSinkCreationDate() != null ? DateFormatUtils.format(sinkBean.getSinkCreationDate(), DATE_FORMAT_DD_MM_YYYY) : StringUtils.EMPTY);
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
