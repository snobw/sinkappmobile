package com.inopek.duvana.sink.tasks;


import android.content.Context;

import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class HttpRequestSendBeanTask extends AbstractHttpRequestTask<Long> {

    private static final String REQUEST_NAME = "/save/";

    private SinkBean sink;
    private boolean checkExists;

    public HttpRequestSendBeanTask(SinkBean sink, Context context, UserBean userBean, boolean checkExists) {
        super(context, userBean);
        this.sink = sink;
        this.checkExists = checkExists;
    }

    @Override
    protected Long post(RestTemplate restTemplate, String url, Map<String, String> mapVariables) {
        mapVariables.put("checkReferenceExits", Boolean.toString(checkExists));
        url += "/{checkReferenceExits}";
        return restTemplate.postForObject(url, sink, Long.class, mapVariables);
    }

    @Override
    protected String getNameRequest() {
        return REQUEST_NAME;
    }

}
