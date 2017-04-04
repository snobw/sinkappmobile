package com.inopek.duvana.sink.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;

import java.util.ArrayList;

public abstract class AbstractSinkBeanAdapter extends ArrayAdapter<SinkBean> {

    protected abstract int getResource();

    protected abstract void populateCustomView(View convertView, SinkBean sinkBean);

    public AbstractSinkBeanAdapter(Context context, ArrayList<SinkBean> sinks) {
        super(context, 0, sinks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SinkBean sinkBean = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(getResource(), parent, false);
        }
        // Lookup view for data population
        TextView referenceTv = (TextView) convertView.findViewById(R.id.referenceTextView);

        // Populate the data into the template view using the data object
        referenceTv.setText(sinkBean.getReference());
        referenceTv.setError(null);
        referenceTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));

        populateCustomView(convertView, sinkBean);

        // Return the completed view to render on screen
        return convertView;
    }
}
