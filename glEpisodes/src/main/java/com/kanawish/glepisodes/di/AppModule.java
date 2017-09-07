package com.kanawish.glepisodes.di;

import android.app.Application;

import com.kanawish.glepisodes.tools.GLHelper;

import toothpick.config.Module;

/**
 * Application level module.
 *
 *
 * GLHelper
 */
public class AppModule extends Module {

    public AppModule(Application app) {
        bind(GLHelper.class);
        // NOTE: Add bindings as needed here.
    }

}