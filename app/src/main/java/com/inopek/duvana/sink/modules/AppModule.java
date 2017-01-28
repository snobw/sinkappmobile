package com.inopek.duvana.sink.modules;

import com.inopek.duvana.sink.services.RequirementValidationService;
import com.inopek.duvana.sink.services.impl.RequirementValidationServiceImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    RequirementValidationService providesRequirementValidationService() {
        return new RequirementValidationServiceImpl();
    }

}