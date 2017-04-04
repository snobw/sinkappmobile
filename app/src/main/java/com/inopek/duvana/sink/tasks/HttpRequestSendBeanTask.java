package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.content.SharedPreferences;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.enums.ProfileEnum;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HttpRequestSendBeanTask extends AbstractHttpRequestTask<Long> {

    private static final String REQUEST_NAME = "/save/";

    private SinkBean sink;
    private boolean checkExists;
    private boolean updateAll;

    public HttpRequestSendBeanTask(SinkBean sink, Context context, UserBean userBean, boolean checkExists, boolean updateAll) {
        super(context, userBean);
        this.sink = sink;
        this.checkExists = checkExists;
        this.updateAll = updateAll;
    }

    @Override
    protected Long post(RestTemplate restTemplate, String url, Map<String, String> mapVariables) {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(context);
        String profile = defaultSharedPreferences.getString(context.getString(R.string.profile_name_preference), context.getString(R.string.profile_name_preference));
        mapVariables.put("checkReferenceExits", Boolean.toString(checkExists));
        mapVariables.put("updateAll", Boolean.toString(updateAll));
        mapVariables.put("stepBefore", ProfileEnum.BEGIN.getLabel().equals(profile) ? String.valueOf(true) : String.valueOf(false));
        url += "/{checkReferenceExits}/{updateAll}/{stepBefore}";
        return restTemplate.postForObject(url, sink, Long.class, mapVariables);
    }

    @Override
    protected String getNameRequest() {
        return REQUEST_NAME;
    }

}
