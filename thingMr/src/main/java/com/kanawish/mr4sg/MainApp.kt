package com.kanawish.mr4sg

import android.app.Application
import com.kanawish.di.ActivityInjectionLifecycle
import com.kanawish.mr4sg.di.openActivityScope
import com.kanawish.mr4sg.di.openApplicationScope
import com.kanawish.mr4sg.grovepi.GrovePiManager
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject

/**
 */
class MainApp : Application() {

    @Inject lateinit var grovePiManager:GrovePiManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } // NOTE: Only logging when running the DEBUG flavor

        Timber.i("%s %d %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ActivityInjectionLifecycle(::openActivityScope))
    }

}