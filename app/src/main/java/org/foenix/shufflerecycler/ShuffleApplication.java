package org.foenix.shufflerecycler;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Foenix on 13.12.2016.
 */

public class ShuffleApplication extends Application {
    private RefWatcher refWatcher;
    public static RefWatcher getRefWatcher(Context context) {
        ShuffleApplication application = (ShuffleApplication) context.getApplicationContext();
        return application.refWatcher;
    }
    public void onCreate() {
        super.onCreate();
        //Stetho.initializeWithDefaults(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

    }
}
