package com.inopek.duvana.sink.activities;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;

import java.util.Date;

public class SinkCreationActivity extends AbstractInputActivity {

    @Override
    protected void addSendListenerAction() {
        SinkBean sinkBean = new SinkBean();
        if (createSinkBean(sinkBean)) {
            runTask(sinkBean, true, false);
        }else {
            // Message d'error
            ActivityUtils.showToastMessage(getString(R.string.required_fields_empty_message), getBaseContext());
        }
    }

    @Override
    protected void populateFromExtras(String extra) {

    }

    @Override
    protected void updateCustomValues(SinkBean sinkBean) {
        sinkBean.setSinkCreationDate(new Date());
    }

    @Override
    protected SinkBean getSinkBeanToSave() {
        return new SinkBean();
    }
}
