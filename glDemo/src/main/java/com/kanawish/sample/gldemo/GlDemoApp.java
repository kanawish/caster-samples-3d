package com.kanawish.sample.gldemo;

import android.app.Application;

import com.kanawish.sample.gldemo.module.ActivityHierarchyServer;
import com.kanawish.sample.gldemo.module.AppModule;
import com.kanawish.sample.gldemo.module.DomainModule;

import javax.inject.Inject;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieApplicationModule;

/**
 */
public class GlDemoApp extends Application {

    @Inject
    ActivityHierarchyServer activityHierarchyServer;

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

        registerActivityLifecycleCallbacks(activityHierarchyServer);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } // NOTE: Only logging when running the DEBUG flavor

        Timber.i("%s %d %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID);

    }
}
