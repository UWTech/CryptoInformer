package com.example.cryptoinformer;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    /**
     * Class that overrides the application class,
     * an allows accessing the application context.
     * Example from Stack Overflow:
     * https://stackoverflow.com/questions/7144177/getting-the-application-context
     */
    public static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}