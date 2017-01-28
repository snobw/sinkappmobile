package com.inopek.duvana.sink.injectors;

import com.inopek.duvana.sink.component.AppComponent;

public class Injector {

    private static Injector ourInstance = new Injector();

    private AppComponent appComponent;

    private Injector() {
    }

    public static Injector getInstance() {
        return ourInstance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void setAppComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }
}
