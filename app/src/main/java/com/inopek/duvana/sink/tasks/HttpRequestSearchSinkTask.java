package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestSearchSinkTask extends AsyncTask<Object, Object, SinkBean[]> {

    private static final int CONNECT_TIMEOUT = 3000;
    private static final String HTTP_PREFIX = "http://";
    private static final String ADDRESS_HOST = "duvana.server.host.address";
    private static final String PORT_HOST = "duvana.server.host.port";
    private static final String REQUEST_NAME = "/search/{startDate}/{endDate}";

    private String startDate;
    private String endDate;
    private Context context;

    public HttpRequestSearchSinkTask(String startDate, String endDate, Context context) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.context = context;
    }

    @Override
    protected SinkBean[] doInBackground(Object... params) {
        try {
            final String url = HTTP_PREFIX + PropertiesUtils.getProperty(ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(PORT_HOST, context) + REQUEST_NAME;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Map<String, String> mapVariables = new HashMap<>();
            mapVariables.put("startDate", startDate.replaceAll("/", "-"));
            mapVariables.put("endDate", endDate.replaceAll("/", "-"));
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(CONNECT_TIMEOUT);
            return restTemplate.getForObject(url, SinkBean[].class, mapVariables);
        } catch (Exception e) {
            Log.e("HttpRequestTask ", e.getMessage(), e);
            return null;
        }
    }

}
