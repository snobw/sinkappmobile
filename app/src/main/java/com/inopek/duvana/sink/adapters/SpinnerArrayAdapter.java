package com.inopek.duvana.sink.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerArrayAdapter extends ArrayAdapter<String> {

    public SpinnerArrayAdapter(Context context, List<String> values) {
        super(context, android.R.layout.simple_spinner_dropdown_item, values);
    }

    public Integer getPositionForItem(String value) {
        int n = this.getCount();
        for (int i = 0; i < n; i++) {
            String status = this.getItem(i);
            if (status.equals(value)) {
                return i;
            }
        }
        return null;
    }
}
