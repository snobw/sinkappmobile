package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.content.SharedPreferences;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.enums.ProfileEnum;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HttpRequestSendFileTask extends AbstractHttpRequestTask<HashMap<String, Boolean>> {

    private static final String REQUEST_NAME = "/saveFromFile/";

    private Set<SinkBean> sinks;

    public HttpRequestSendFileTask(Set<SinkBean> sinks, Context context, UserBean userBean) {
        super(context, userBean);
        this.sinks = sinks;
    }

    @Override
    protected HashMap<String, Boolean> post(RestTemplate restTemplate, String url, Map<String, String> mapVariables) {

        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        String profile = sharedPref.getString(context.getString(R.string.profile_name_preference), context.getString(R.string.profile_name_preference));
        url += "/{profile}";
        mapVariables.put("profile", profile);
        return restTemplate.postForObject(url, sinks, HashMap.class, mapVariables);
    }

    @Override
    protected String getNameRequest() {
        return REQUEST_NAME;
    }

}
