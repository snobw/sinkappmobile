package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.enums.ProfileEnum;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HttpRequestSearchPairReferenceClientTask extends AsyncTask<Void, Void, Boolean> {

    private static final String REQUEST_NAME = "/searchPairReferenceClient/{clientName}/{reference}/{stepBefore}";

    private SinkBean sinkBean;
    private Context context;

    public HttpRequestSearchPairReferenceClientTask(SinkBean sinkBean, Context context) {
        this.sinkBean = sinkBean;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(context);
            String profile = defaultSharedPreferences.getString(context.getString(R.string.profile_name_preference), context.getString(R.string.profile_name_preference));
            final String url = SinkConstants.HTTP_PREFIX + PropertiesUtils.getProperty(SinkConstants.ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(SinkConstants.PORT_HOST, context) + REQUEST_NAME;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Map<String, String> mapVariables = new HashMap<>();
            mapVariables.put("reference", sinkBean.getReference());
            mapVariables.put("stepBefore", ProfileEnum.BEGIN.getLabel().equals(profile) ? String.valueOf(true) : String.valueOf(false));
            mapVariables.put("clientName", sinkBean.getClient().getName());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(SinkConstants.CONNECT_TIMEOUT);
            return restTemplate.getForObject(url, Boolean.class, mapVariables);
        } catch (Exception e) {
            Log.e("HttpRequestTask ", e.getMessage(), e);
            return null;
        }
    }
}
