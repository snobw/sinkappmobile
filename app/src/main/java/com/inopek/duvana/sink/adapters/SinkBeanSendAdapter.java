package com.inopek.duvana.sink.adapters;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;
import static com.inopek.duvana.sink.constants.SinkConstants.DATE_FORMAT_DD_MM_YYYY;

public class SinkBeanSendAdapter extends AbstractSinkBeanAdapter {

    private Set<SinkBean> sinks = new HashSet<>();

    private List<CheckBox> checkBoxes = new ArrayList<>();

    private HashMap<String, Boolean> fileNamesMap;

    int resource;

    public SinkBeanSendAdapter(Context context, ArrayList<SinkBean> sinks, int resource) {
        super(context, sinks);
        this.resource = resource;
    }

    public SinkBeanSendAdapter(Context context, ArrayList<SinkBean> sinks, int resource, HashMap<String, Boolean> fileNamesMap) {
        super(context, sinks);
        this.resource = resource;
        this.fileNamesMap = fileNamesMap;
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
        TextView referenceTv = (TextView) convertView.findViewById(R.id.referenceTextView);

        if (fileNamesMap != null && !fileNamesMap.isEmpty() && fileNamesMap.get(sinkBean.getFileName()) != null && sinkBean.getReference().equals(referenceTv.getText().toString())) {
            referenceTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorError));
            referenceTv.setError(getContext().getString(R.string.reference_exists_save_message));
            ActivityUtils.showToastMessageLong(getContext().getString(R.string.double_exists_save_message), getContext());
        }
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
