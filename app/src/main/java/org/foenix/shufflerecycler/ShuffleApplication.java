package org.foenix.shufflerecycler;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by Foenix on 13.12.2016.
 */

public class ShuffleApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ShuffleApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);
    }

    public static Context getAppContext() {
        return ShuffleApplication.context;
    }
}
