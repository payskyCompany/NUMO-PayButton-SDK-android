package com.example.amrel.paybuttonexample;

import androidx.multidex.MultiDexApplication;

import io.paysky.paybutton.data.network.ApiConnection;

/**
 * Application class. Initializes Chucker for HTTP inspection in debug builds.
 */
public class PayButtonApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiConnection.initializeChucker(this);
    }
}
