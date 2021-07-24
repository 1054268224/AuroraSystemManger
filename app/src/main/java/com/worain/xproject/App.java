package com.worain.xproject;

import android.app.Application;

import com.worain.xproject.dialog.WtkDialog;

/**
 * <pre>
 *     author : shuai.hu
 *     time   : 2021/07/20
 *     desc   : ...
 * </pre>
 */
public class App extends Application {
    private static App instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(WtkDialog.getInstance().createLifeCallback());
    }
}
