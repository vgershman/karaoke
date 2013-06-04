package com.expelabs.karaoke.app;

import android.app.Application;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class KaraokeApp extends Application {

    private static Context context;
    public static final String PREFERENCES_NAME = "karaoke";
    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
