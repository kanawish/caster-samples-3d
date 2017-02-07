package com.kanawish.glepisodes.module.app;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * General spot for all generic helper 'functions' we come up with
 * during the series.
 *
 * GLHelper will have static methods, but also activity context aware ones.
 *
 * We use app-scoped dependency injection here, to keep things clean.
 */
@Singleton
public class GLHelper {

    @Inject ActivityManager activityManager;

    protected GLHelper() {
    }

    public boolean isEsVersionSupported(int majorVersion, int minorVersion) {
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        int deviceMajor = configurationInfo.reqGlEsVersion & 0xFFFF0000 >> 4;
        int deviceMinor = configurationInfo.reqGlEsVersion & 0xFFFF ;

        return ((majorVersion <= deviceMajor) && (minorVersion <= deviceMinor));
    }

}