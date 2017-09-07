package com.kanawish.glepisodes

import android.app.Application
import com.kanawish.glepisodes.di.openApplicationScope
import com.kanawish.sample.mvi.di.ActivityInjectionLifecycle
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject

/**
 */
class GlDemoApp : Application() {

    @Inject lateinit var activityInjectionLifecycle: ActivityLifecycleCallbacks

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } // NOTE: Only logging when running the DEBUG flavor

        Timber.i("%s %d %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(activityInjectionLifecycle)
    }
}
