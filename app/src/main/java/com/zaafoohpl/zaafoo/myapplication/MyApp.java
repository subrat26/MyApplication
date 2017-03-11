package com.zaafoohpl.zaafoo.myapplication;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by SUB on 3/9/2017.
 */

public class MyApp extends MultiDexApplication {


    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);


    }
}
