package com.inopek.duvana.sink.activities;


import com.inopek.duvana.sink.beans.SinkBean;

public class SinkBasicCreationActivity extends AbstractBasicCreationActivity {

    @Override
    protected SinkBean getSinkBeanToSave() {
        return new SinkBean();
    }

    @Override
    protected void populateFromExtras(String extra) {

    }
}
