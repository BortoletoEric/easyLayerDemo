package br.com.gertec.easylayer.app;

import android.app.Application;

import br.com.gertec.gedi.GEDI;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GEDI.init(this);
    }
}
