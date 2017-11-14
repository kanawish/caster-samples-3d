package com.kanawish.glepisodes.di

import android.app.Application
import com.kanawish.glepisodes.module.domain.FileSystemManager
import com.kanawish.glepisodes.module.domain.ScriptManager
import com.kanawish.di.ActivityInjectionLifecycle
import toothpick.config.Module

class AppModule(appContext: Application) : Module() {

    init {
        bind(Application.ActivityLifecycleCallbacks::class.java).to(ActivityInjectionLifecycle::class.java)
        bind(ScriptManager::class.java).to(FileSystemManager::class.java)
    }
}