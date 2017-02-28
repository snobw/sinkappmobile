package com.inopek.duvana.sink.modules;

import com.inopek.duvana.sink.services.CustomService;
import com.inopek.duvana.sink.services.impl.CustomServiceImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    CustomService providesCustomService() {
        return new CustomServiceImpl();
    }

}