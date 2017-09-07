package com.kanawish.glepisodes;

import android.app.Application;

import com.kanawish.glepisodes.module.DomainModule;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieApplicationModule;

import static com.kanawish.glepisodes.di.ScopesKt.openApplicationScope;

/**
 */
public class GlDemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Scope appScope = Toothpick.openScope(this);
        appScope.installModules(
                new SmoothieApplicationModule(this),
                new AppModule(),
                new DomainModule()
        );
        Toothpick.inject(this, appScope);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } // NOTE: Only logging when running the DEBUG flavor

        Timber.i("%s %d %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID);

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ToothpickLifecycle())

    }
}
