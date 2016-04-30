package com.tixon.smarthouseworking;

import android.app.Application;

import com.tixon.smarthouseworking.database.HelperFactory;

/**
 * Created by tikhon on 30.04.16
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
