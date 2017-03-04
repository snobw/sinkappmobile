package com.inopek.duvana.sink.tasks;


import android.content.Context;

import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpRequestSendFileTask extends AbstractHttpRequestTask<List<String>> {

    private static final String REQUEST_NAME = "/saveFromFile/";

    private Set<SinkBean> sinks;

    public HttpRequestSendFileTask(Set<SinkBean> sinks, Context context, UserBean userBean) {
        super(context, userBean);
        this.sinks = sinks;
    }

    @Override
    protected List<String> post(RestTemplate restTemplate, String url, Map<String, String> mapVariables) {
        return restTemplate.postForObject(url, sinks, List.class, mapVariables);
    }

    @Override
    protected String getNameRequest() {
        return REQUEST_NAME;
    }

}
