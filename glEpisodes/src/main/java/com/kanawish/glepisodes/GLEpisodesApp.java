package com.kanawish.glepisodes;

import android.app.Application;

import com.kanawish.glepisodes.module.ScopeBuilder;

import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

/**
 * We setup Toothpick dependency injection and Timber simplified logging
 * via this custom Application implementation.
 */
public class GLEpisodesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Scope appScope = ScopeBuilder.buildApplicationScope(this);
        Toothpick.inject(this, appScope);

        // NOTE: We only want logs when running the DEBUG flavor.
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Banner to help reduce mixups over what version is running, etc.
        Timber.i("%s %s %d %s", BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID);
    }
}