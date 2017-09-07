package com.kanawish.di

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import toothpick.Scope
import toothpick.Toothpick

/**
 */

class ActivityInjectionLifecycle(private val scopeBuilder: (Activity) -> Scope) : ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        activity?.apply {
            Toothpick.inject(this, scopeBuilder(activity))
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.apply {
            Toothpick.closeScope(this)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
    }
}