package com.test.gpstracker;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by salimi on 10/1/2016.
 */
public class MainApp extends MultiDexApplication {

    private static MainApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        MultiDex.install(mInstance);
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    public static synchronized MainApp getInstance() {
        return mInstance;
    }

}
