package com.kanawish.glepisodes.di

import android.app.Application
import com.kanawish.glepisodes.module.domain.FileSystemManager
import com.kanawish.glepisodes.module.domain.ScriptManager
import toothpick.config.Module

class AppModule(appContext: Application) : Module() {

    init {
//        bind(TaskDb::class.java).toInstance(Room.databaseBuilder(appContext, TaskDb::class.java, "taskDb").build())
//        bind(TaskRepo::class.java).to(BasicTaskRepo::class.java)

        bind(ScriptManager::class.java).to(FileSystemManager::class.java)

        // TODO: Add bindings.
        // bind()
    }
}