package com.inopek.duvana.sink.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.inopek.duvana.sink.PropertiesUtils;
import com.inopek.duvana.sink.beans.SinkBean;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpRequestSaveTask extends AsyncTask<Void, Void, SinkBean> {

    private final SinkBean sinkBean;
    private final Context context;
    private static final String HTTP_PREFIX = "http://";
    private static final String ADDRESS_HOST = "duvana.server.host.address";
    private static final String PORT_HOST = "duvana.server.host.port";

    public HttpRequestSaveTask(SinkBean sinkBean, Context context) {
        super();
        this.sinkBean = sinkBean;
        this.context = context;
    }

    @Override
    protected SinkBean doInBackground(Void... params) {
        try {
            final String url = HTTP_PREFIX + PropertiesUtils.getProperty(ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(PORT_HOST, context) + "/save";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            SinkBean resultSinkBean = restTemplate.postForObject(url, sinkBean, SinkBean.class);
//            SinkBean sinkBean = restTemplate.getForObject(url, SinkBean.class);
            return resultSinkBean;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
            return null;
        }
    }
}
