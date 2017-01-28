package com.inopek.duvana.sink.component;

import com.inopek.duvana.sink.MainActivity;
import com.inopek.duvana.sink.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity activity);
}