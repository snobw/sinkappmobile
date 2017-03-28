package com.inopek.duvana.sink.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.inopek.duvana.sink.beans.ClientReferenceBean;
import com.inopek.duvana.sink.handler.ReferenceClientHandler;

public class ReferenceClientDao extends AbstractDao<ReferenceClientHandler> {

    private final static String BDD_NAME = "database.db";
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_REF = 1;
    private static final int NUM_COL_CLIENT = 2;
    private static final int NUM_COL_FILE_NAME = 3;
    private static final int NUM_COL_PROFILE_NAME = 4;
    private final static int VERSION = 1;

    public ReferenceClientDao(Context context) {
        // create data base
        mHandler = new ReferenceClientHandler(context, BDD_NAME, null, VERSION);
    }

    public void add(ClientReferenceBean bean) {
        ContentValues values = new ContentValues();
        values.put(ReferenceClientHandler.COLUMN_REFERENCE, bean.getReference());
        values.put(ReferenceClientHandler.COLUMN_CLIENT_NAME, bean.getClientName());
        values.put(ReferenceClientHandler.COLUMN_FILE_NAME, bean.getFileName());
        values.put(ReferenceClientHandler.COLUMN_PROFILE_NAME, bean.getProfileName());
        getDb().insert(ReferenceClientHandler.TABLE_NAME, null, values);
//        getDb().insertWithOnConflict(ReferenceClientHandler.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public void delete(ClientReferenceBean clientReferenceBean) {
        String query = ReferenceClientHandler.COLUMN_REFERENCE + " = \"" + clientReferenceBean.getReference() + "\"" + " AND " + ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientReferenceBean.getClientName() + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + clientReferenceBean.getProfileName() + "\"";
        getDb().delete(ReferenceClientHandler.TABLE_NAME, query, null);
    }

    public ClientReferenceBean getByReferenceAndClientName(String reference, String clientName, String profileName) {
        String query = ReferenceClientHandler.COLUMN_REFERENCE + " = \"" + reference + "\"" + " AND " + ReferenceClientHandler.COLUMN_CLIENT_NAME + " = \"" + clientName + "\"" + " AND " + ReferenceClientHandler.COLUMN_PROFILE_NAME + " = \"" + profileName + "\"";
        Cursor c = getDb().query(ReferenceClientHandler.TABLE_NAME, new String[]{ReferenceClientHandler.KEY, ReferenceClientHandler.COLUMN_REFERENCE, ReferenceClientHandler.COLUMN_CLIENT_NAME, ReferenceClientHandler.COLUMN_FILE_NAME, ReferenceClientHandler.COLUMN_PROFILE_NAME}, query, null, null, null, null);
        return cursorToClientReferenceBean(c);
    }

    private ClientReferenceBean cursorToClientReferenceBean(Cursor cursor) {
        // nothing found
        if (cursor.getCount() == 0) {
            return null;
        }

        // move to first
        cursor.moveToFirst();
        // create bean
        ClientReferenceBean clientReferenceBean = new ClientReferenceBean();

        clientReferenceBean.setId(cursor.getLong(NUM_COL_ID));
        clientReferenceBean.setReference(cursor.getString(NUM_COL_REF));
        clientReferenceBean.setClientName(cursor.getString(NUM_COL_CLIENT));
        clientReferenceBean.setFileName(cursor.getString(NUM_COL_FILE_NAME));
        clientReferenceBean.setProfileName(cursor.getString(NUM_COL_PROFILE_NAME));

        // close cursor
        cursor.close();

        return clientReferenceBean;
    }
}
