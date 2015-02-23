package com.example.TestVlc;

import android.app.Application;
import android.content.Context;

/**
 * Created by YDP on 2015/2/22.
 */
public class MyApp extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
