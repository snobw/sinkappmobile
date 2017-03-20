package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpRequestDeleteSinkTask extends AsyncTask<Void, Void, Boolean> {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final String HTTP_PREFIX = "http://";
    private static final String ADDRESS_HOST = "duvana.server.host.address";
    private static final String PORT_HOST = "duvana.server.host.port";
    private static final String REQUEST_NAME = "/delete";

    private SinkBean sinkBean;
    private Context context;

    public HttpRequestDeleteSinkTask(SinkBean sinkBean, Context context) {
        this.sinkBean = sinkBean;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            final String url = HTTP_PREFIX + PropertiesUtils.getProperty(ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(PORT_HOST, context) + REQUEST_NAME;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(CONNECT_TIMEOUT);
            return restTemplate.postForObject(url, sinkBean, Boolean.class);
        } catch (Exception e) {
            Log.e("HttpRequestTask ", e.getMessage(), e);
            return null;
        }
    }
}
