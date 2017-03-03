package com.inopek.duvana.sink.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.inopek.duvana.sink.utils.PropertiesUtils;
import com.inopek.duvana.sink.beans.SinkBean;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

public class HttpRequestSaveTask extends AsyncTask<Void, Void, SinkBean> {

    private static final int CONNECT_TIMEOUT = 10000;
    private static final String HTTP_PREFIX = "http://";
    private static final String ADDRESS_HOST = "duvana.server.host.address";
    private static final String PORT_HOST = "duvana.server.host.port";
    private Set<SinkBean> sinks;
    private final Context context;
    private ProgressDialog dialog = null;

    public HttpRequestSaveTask(SinkBean sinkBean, Context context) {
        super();
        this.context = context;
        sinks = new HashSet<>();
        sinks.add(sinkBean);
    }

    public HttpRequestSaveTask(Set<SinkBean> sinks, Context context) {
        super();
        this.sinks = sinks;
        this.context = context;
    }

    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.setMessage("Enviando reporte...");
        dialog.show();
    }

    @Override
    protected SinkBean doInBackground(Void... params) {
        try {
            final String url = HTTP_PREFIX + PropertiesUtils.getProperty(ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(PORT_HOST, context) + "/save";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(CONNECT_TIMEOUT);
            // TODO check result the delete files
            restTemplate.postForObject(url, sinks, SinkBean.class);


//            SinkBean sinkBean = restTemplate.getForObject(url, SinkBean.class);
            return null;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(SinkBean sinkBean) {
        // TODO update signature
        if (dialog.isShowing()) { // if dialog box showing = true
            dialog.dismiss(); // dismiss it
        }
        if (sinkBean == null) {
            dialog = new ProgressDialog(context);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setMessage("Un error se produjo y el reporte no pudo ser enviado...");
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
