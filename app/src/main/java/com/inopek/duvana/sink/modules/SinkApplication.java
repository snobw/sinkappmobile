package com.inopek.duvana.sink.modules;

import android.app.Application;

import com.inopek.duvana.sink.component.DaggerAppComponent;
import com.inopek.duvana.sink.injectors.Injector;

public class SinkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // DaggerAppComponent est une classe générée automatiquement après une compilation
        Injector.getInstance().setAppComponent(DaggerAppComponent.create());
    }
}