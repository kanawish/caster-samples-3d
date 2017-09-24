package com.kanawish.glepisodes.di

import android.app.Activity
import android.app.Application
import com.kanawish.di.ActivitySingleton

import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import toothpick.smoothie.module.SmoothieApplicationModule

/**
 *
 */
fun openApplicationScope(app: Application): Scope = Toothpick.openScope(app).apply {
    installModules(
            SmoothieApplicationModule(app),
            AppModule(app))
}

/**
 *
 */
fun openActivityScope(activity: Activity): Scope = Toothpick.openScopes(activity.application, activity).apply {
    // https://github.com/stephanenicolas/toothpick/wiki/Scope-Annotations
    bindScopeAnnotation(ActivitySingleton::class.java)

    installModules(
            SmoothieActivityModule(activity),
            ActivityModule(activity))
}
