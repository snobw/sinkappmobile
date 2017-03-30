package com.inopek.duvana.sink.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.inopek.duvana.sink.beans.ClientReferenceBean;
import com.inopek.duvana.sink.handler.ReferenceClientHandler;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReferenceClientDao extends AbstractDao<ReferenceClientHandler> {

    private final static String BDD_NAME = "database.db";
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_REF = 1;
    private static final int NUM_COL_CLIENT = 2;
    private static final int NUM_COL_FILE_NAME = 3;
    private static final int NUM_COL_PROFILE_NAME = 4;
    private static final int NUM_COL_TIMESTAMP = 5;
    private final static int VERSION = 1;
    public static final String[] ALL_COLUMNS = {ReferenceClientHandler.COLUMN_KEY, ReferenceClientHandler.COLUMN_REFERENCE, ReferenceClientHandler.COLUMN_CLIENT_NAME, ReferenceClientHandler.COLUMN_FILE_NAME, ReferenceClientHandler.COLUMN_PROFILE_NAME, ReferenceClientHandler.COLUMN_DATE_NAME};

    public ReferenceClientDao(Context context) {
        // create data base
        mHandler = new ReferenceClientHandler(context, BDD_NAME, null, VERSION);
    }

    public void add(ClientReferenceBean bean) {
        DateTime dateTime = DateTime.now().withTimeAtStartOfDay();
        ContentValues values = new ContentValues();
        values.put(ReferenceClientHandler.COLUMN_REFERENCE, bean.getReference());
        values.put(ReferenceClientHandler.COLUMN_CLIENT_NAME, bean.getClientName());
        values.put(ReferenceClientHandler.COLUMN_FILE_NAME, bean.getFileName());
        values.put(ReferenceClientHandler.COLUMN_PROFILE_NAME, bean.getProfileName());
        values.put(ReferenceClientHandler.COLUMN_DATE_NAME, String.valueOf(dateTime.getMillis()));
        getDb().insert(ReferenceClientHandler.TABLE_NAME, null, values);
    }

    public void update(ClientReferenceBean clientReferenceBean) {
        String query = ReferenceClientHandler.COLUMN_KEY + " = \"" + clientReferenceBean.getId() + "\"" + " AND " + ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientReferenceBean.getClientName() + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + clientReferenceBean.getProfileName() + "\"";
        ContentValues values = new ContentValues();
        values.put(ReferenceClientHandler.COLUMN_REFERENCE, clientReferenceBean.getReference());
        getDb().update(ReferenceClientHandler.TABLE_NAME, values, query, null);
    }

    public void delete(ClientReferenceBean clientReferenceBean) {
        String query = ReferenceClientHandler.COLUMN_REFERENCE + " = \"" + clientReferenceBean.getReference() + "\"" + " AND " + ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientReferenceBean.getClientName() + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + clientReferenceBean.getProfileName() + "\"";
        getDb().delete(ReferenceClientHandler.TABLE_NAME, query, null);
    }

    public void deleteByFileName(String fileName) {
        String query = ReferenceClientHandler.COLUMN_FILE_NAME + " = \"" + fileName + "\"";
        getDb().delete(ReferenceClientHandler.TABLE_NAME, query, null);
    }

    public ClientReferenceBean getByReferenceAndClientName(String reference, String clientName, String profileName) {
        String query = ReferenceClientHandler.COLUMN_REFERENCE + " = \"" + reference + "\"" + " AND " + ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientName + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + profileName + "\"";
        Cursor c = getDb().query(ReferenceClientHandler.TABLE_NAME, ALL_COLUMNS, query, null, null, null, null);
        return cursorToClientReferenceBean(c);
    }

    public List<ClientReferenceBean> getByFileName(String fileName) {
        List<ClientReferenceBean> resultats = new ArrayList<>();
        String query = ReferenceClientHandler.COLUMN_FILE_NAME + " = \"" + fileName + "\"";
        return executeQueryAndCreateResultList(resultats, query);
    }

    public List<ClientReferenceBean> getByClientNameAndProfile(String clientName, String profileName) {
        List<ClientReferenceBean> resultats = new ArrayList<>();
        String query = ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientName + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + profileName + "\"";
        return executeQueryAndCreateResultList(resultats, query);
    }

    public List<ClientReferenceBean> getByClientNameAndProfileByDateAndReferene(String clientName, String profileName, Date startDate, Date endDate, String reference) {
        List<ClientReferenceBean> resultats = new ArrayList<>();
        String query = ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientName + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + profileName + "\"";
        query += " AND " + ReferenceClientHandler.COLUMN_DATE_NAME + " BETWEEN " + startDate.getTime() + " AND " + endDate.getTime() + "";
        if (StringUtils.isNotEmpty(reference)) {
            query += " AND " + ReferenceClientHandler.COLUMN_REFERENCE + " = " + reference + "";
        }
        return executeQueryAndCreateResultList(resultats, query);
    }

    private List<ClientReferenceBean> executeQueryAndCreateResultList(List<ClientReferenceBean> resultats, String query) {
        Cursor cursor = getDb().query(ReferenceClientHandler.TABLE_NAME, ALL_COLUMNS, query, null, null, null, null);
        while(cursor.moveToNext()) {
            resultats.add(createClientReferenceBean(cursor));
        }
        return resultats;
    }

    private ClientReferenceBean cursorToClientReferenceBean(Cursor cursor) {
        // nothing found
        if (cursor.getCount() == 0) {
            return null;
        }

        // move to first
        cursor.moveToFirst();
        // create bean
        ClientReferenceBean clientReferenceBean = createClientReferenceBean(cursor);
        // close cursor
        cursor.close();

        return clientReferenceBean;
    }

    private ClientReferenceBean createClientReferenceBean(Cursor cursor) {
        ClientReferenceBean clientReferenceBean = new ClientReferenceBean();
        clientReferenceBean.setId(cursor.getLong(NUM_COL_ID));
        clientReferenceBean.setReference(cursor.getString(NUM_COL_REF));
        clientReferenceBean.setClientName(cursor.getString(NUM_COL_CLIENT));
        clientReferenceBean.setFileName(cursor.getString(NUM_COL_FILE_NAME));
        clientReferenceBean.setProfileName(cursor.getString(NUM_COL_PROFILE_NAME));
        clientReferenceBean.setProfileName(cursor.getString(NUM_COL_PROFILE_NAME));
        clientReferenceBean.setTimestamp(cursor.getLong(NUM_COL_TIMESTAMP));
        return clientReferenceBean;
    }
}
