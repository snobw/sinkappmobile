package com.inopek.duvana.sink;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

    private PropertiesUtils() {
    }

    public static String getProperty(String key, Context context) throws IOException {

        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("configuration.properties");
        properties.load(inputStream);
        return properties.getProperty(key);

    }
}
