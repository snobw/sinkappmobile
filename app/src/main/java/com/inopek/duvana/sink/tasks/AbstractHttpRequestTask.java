package com.inopek.duvana.sink.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class AbstractHttpRequestTask<T extends Object> extends AsyncTask<Void, Void, T> {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final String HTTP_PREFIX = "http://";
    private static final String ADDRESS_HOST = "duvana.server.host.address";
    private static final String PORT_HOST = "duvana.server.host.port";
    private final Context context;
    private UserBean userBean;

    protected abstract T post(RestTemplate restTemplate, String url, Map<String, String> mapVariables);

    protected abstract String getNameRequest();

    public AbstractHttpRequestTask(Context context, UserBean userBean) {
        super();
        this.context = context;
        this.userBean = userBean;
    }

    @Override
    protected T doInBackground(Void... params) {
        try {
            final String url = HTTP_PREFIX + PropertiesUtils.getProperty(ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(PORT_HOST, context) + getNameRequest() + "{userImi}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            Map<String, String> mapVariables = new HashMap<>();
            mapVariables.put("userImi", userBean.getImiNumber());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(CONNECT_TIMEOUT);
            return post(restTemplate, url, mapVariables);
        } catch (Exception e) {
            Log.e("HttpRequestTask ", e.getMessage(), e);
            return null;
        }
    }
}
