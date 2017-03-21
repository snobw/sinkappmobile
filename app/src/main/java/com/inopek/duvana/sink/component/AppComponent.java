package com.inopek.duvana.sink.component;

import com.inopek.duvana.sink.activities.AbstractCreationActivity;
import com.inopek.duvana.sink.activities.MainActivity;
import com.inopek.duvana.sink.activities.SinkBeforeCreationActivity;
import com.inopek.duvana.sink.activities.SinkCreationActivity;
import com.inopek.duvana.sink.activities.SinkSearchActivity;
import com.inopek.duvana.sink.activities.SinkSendActivity;
import com.inopek.duvana.sink.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(SinkCreationActivity activity);

    void inject(SinkSendActivity activity);

    void inject(SinkBeforeCreationActivity activity);

    void inject(SinkSearchActivity activity);

    void inject(AbstractCreationActivity activity);
}