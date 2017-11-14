package com.kanawish.glepisodes.tools

import android.app.ActivityManager
import android.content.pm.ConfigurationInfo

import javax.inject.Inject
import javax.inject.Singleton

/**
 * General spot for all generic helper 'functions' we come up with
 * during the series.
 *
 * GLHelper will have static methods, but also activity context aware ones.
 *
 * We use app-scoped dependency injection here, to keep things clean.
 */
@Singleton
class GLHelper protected constructor() {

    @Inject lateinit var activityManager: ActivityManager

    fun isEsVersionSupported(majorVersion: Int, minorVersion: Int): Boolean {
        val configurationInfo = activityManager.deviceConfigurationInfo
        val deviceMajor = configurationInfo.reqGlEsVersion and (0xFFFF0000.toInt() shr 4)
        val deviceMinor = configurationInfo.reqGlEsVersion and 0xFFFF

        return majorVersion <= deviceMajor && minorVersion <= deviceMinor
    }

}