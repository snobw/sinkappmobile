package com.inopek.duvana.sink.activities;

import android.content.Context;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.activities.utils.ActivityUtils;
import com.inopek.duvana.sink.beans.SinkBean;

import java.util.Date;

import static com.inopek.duvana.sink.activities.utils.ActivityUtils.showToastMessage;

public class SinkCreationActivity extends AbstractInputActivity {

    @Override
    protected void addSendListenerAction() {
        SinkBean sinkBean = new SinkBean();
        Context context = getBaseContext();
        if (!ActivityUtils.isNetworkAvailable(context)) {
            showToastMessage(getString(R.string.no_network_available_message), context);
        } else if (createSinkBean(sinkBean)) {
            runTask(sinkBean, true, false);
        }else {
            // Message d'error
            ActivityUtils.showToastMessage(getString(R.string.required_fields_empty_message), context);
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

    @Override
    protected boolean isModeEdition() {
        return false;
    }
}
