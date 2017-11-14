package com.kanawish.glepisodes

import android.app.Application
import com.kanawish.di.ActivityInjectionLifecycle
import com.kanawish.glepisodes.di.openActivityScope
import com.kanawish.glepisodes.di.openApplicationScope
import timber.log.Timber
import toothpick.Toothpick

/**
 * We setup Toothpick dependency injection and Timber simplified logging
 * via this custom Application implementation.
 */
class GLEpisodesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // NOTE: We only want logs when running the DEBUG flavor.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Banner to help reduce mixups over what version is running, etc.
        Timber.i("%s %s %d %s", BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ActivityInjectionLifecycle(::openActivityScope))

    }
}