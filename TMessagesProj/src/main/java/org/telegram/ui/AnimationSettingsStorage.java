package org.telegram.ui;

import android.content.Context;
import android.content.ContextWrapper;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AnimationSettingsStorage {

    private static final String TAG = "AnimationSettingsStorage";
    private final Gson gson = new GsonBuilder().create();
    private ContextWrapper contextWrapper;

    void setContext(Context context) {
        contextWrapper = new ContextWrapper(context);
    }

    void save(AnimationSettings animationSettings) {
        BufferedWriter writer = null;
        try {
            String jsonString = gson.toJson(animationSettings);
            Log.d(TAG, "save() json: " + jsonString);
            File settingsFile = getSettingsFile();
            if (settingsFile.exists()) {
                settingsFile.delete();
            }
            settingsFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(settingsFile));
            writer.write(jsonString);
        }
        catch (Exception ex) {
            Log.e(TAG, "save() exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    AnimationSettings load() {
        InputStream inputStream = null;
        try {
            File settingsFile = getSettingsFile();
            inputStream = new FileInputStream(settingsFile);
            String jsonString = readToEnd(inputStream);
            Log.d(TAG, "load() json: " + jsonString);
            AnimationSettings animationSettings = gson.fromJson(jsonString, AnimationSettings.class);
            Log.d(TAG, "load() animationSettings: " + animationSettings);
            if (animationSettings != null) {
                return animationSettings;
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "load() exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return new AnimationSettings();
    }

    private static String readToEnd(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private File getSettingsFile() {
        return new File(contextWrapper.getFilesDir(), "animation_settings.json");
    }
}
