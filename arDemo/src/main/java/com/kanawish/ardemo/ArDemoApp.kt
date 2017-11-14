package com.kanawish.ardemo

import android.app.Application
import com.kanawish.ardemo.di.openActivityScope
import com.kanawish.ardemo.di.openApplicationScope
import com.kanawish.di.ActivityInjectionLifecycle
import timber.log.Timber
import toothpick.Toothpick

/**
 */
class ArDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (com.kanawish.ardemo.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } // NOTE: Only logging when running the DEBUG flavor

        Timber.i("%s %d %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ActivityInjectionLifecycle(::openActivityScope))
    }
}