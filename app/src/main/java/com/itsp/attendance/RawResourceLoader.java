package com.itsp.attendance;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RawResourceLoader {
    private static final String TAG = "RawResourceLoader";

    public static String loadRawResourceKey(Context context, int path, String key) {
        String result = null;

        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            result = properties.getProperty("url");

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the file specified: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open the file.");
        }
        return result;
    }
}
